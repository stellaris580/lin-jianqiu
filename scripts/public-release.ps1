param(
  [string]$Message = "",
  [string]$Tag = "",
  [string]$AuditEvidence = "",
  [switch]$Publish,
  [switch]$DryRun
)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

# Fail closed: every worktree file (not only tracked files) must be explicitly allowlisted.
$allow = @(Get-Content PUBLIC_SOURCE_ALLOWLIST.txt | Where-Object { $_ -and -not $_.StartsWith('#') })
$candidates = @(Get-ChildItem . -Recurse -File -Force |
  Where-Object { $_.FullName -notmatch '\\\.git\\|\\build\\' } |
  ForEach-Object { $_.FullName.Replace($root, '').Replace('\', '/').TrimStart('/') } |
  Sort-Object)
$bad = @()
foreach ($f in $candidates) {
  if ($f -match '\.(apk|aab|db|sqlite|log|keystore|jks|p12)$') { $bad += "FORBIDDEN_EXT $f" }
  if ($allow -notcontains $f) { $bad += "NOT_ALLOWLISTED $f" }
}
foreach ($f in $allow) {
  if ($f -ne 'SOURCE_MANIFEST.sha256' -and $candidates -notcontains $f) { $bad += "ALLOWLIST_MISSING $f" }
}
$pattern = 'sk-[A-Za-z0-9]{16,}|gh[pousr]_[A-Za-z0-9]{20,}|BEGIN (RSA|OPENSSH|EC) PRIVATE KEY'
foreach ($f in $candidates) {
  try { $hit = Select-String -Path (Join-Path $root $f) -Pattern $pattern -Quiet -ErrorAction Stop } catch { $bad += "SCAN_ERROR $f"; continue }
  if ($hit) {
    $bad += "SECRET_PATTERN $f"
  }
}
if ($bad.Count -gt 0) { $bad | Write-Output; throw "PUBLISH_GATE=BLOCKED" }

$javac = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin/javac.exe" } else { "javac" }
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin/java.exe" } else { "java" }
New-Item -ItemType Directory -Force build/classes | Out-Null
& $javac -encoding UTF-8 -d build/classes src/main/java/org/example/life/LifeLoopDemo.java
$out = & $java -cp build/classes org.example.life.LifeLoopDemo
$out | Write-Output
if (-not ($out | Select-String -SimpleMatch 'DEMO_RESULT=PASS')) { throw "DEMO_RESULT!=PASS" }

$manifest = ($candidates | Where-Object { $_ -ne 'SOURCE_MANIFEST.sha256' }) | ForEach-Object {
  $h = (Get-FileHash (Join-Path $root $_) -Algorithm SHA256).Hash.ToLower()
  "$h  $_"
}
[IO.File]::WriteAllText((Join-Path $root 'SOURCE_MANIFEST.sha256'), (($manifest -join "`n") + "`n"), [Text.UTF8Encoding]::new($false))
Write-Output "PRIVACY_GATE=PASS"
Write-Output "LICENSE_CHECK=PASS"
Write-Output "BUILD=PASS"
Write-Output "TEST=PASS"
Write-Output "WHITELIST_EXPORT=PASS"

if ($DryRun -or -not $Publish) { Write-Output "AUTO_PUBLISH=DRY_RUN"; exit 0 }

# Independent audit is mandatory for a real publish; no self-issued evidence.
if (-not $AuditEvidence -or -not (Test-Path $AuditEvidence)) { throw "AUTO_PUBLISH=BLOCKED MISSING_INDEPENDENT_AUDIT" }
$audit = Get-Content $AuditEvidence -Raw
if ($audit -notmatch '(?m)^PRIVACY_AUDIT=PASS[ \t\r]*$' -or $audit -notmatch '(?m)^INDEPENDENT_AUDIT=PASS[ \t\r]*$') {
  throw "AUTO_PUBLISH=BLOCKED AUDIT_NOT_PASS"
}
if ($Message -and (git status --short | Measure-Object).Count -gt 0) {
  git add -A
  git -c user.name="Lin Jianqiu OSS" -c user.email="oss@example.invalid" commit -m $Message
}
git push origin main
if ($Tag) {
  if (git tag -l $Tag) { throw "TAG_ALREADY_EXISTS $Tag" }
  git tag -a $Tag -m "Automated public release $Tag"
  git push origin $Tag
}
Write-Output "AUTO_PUBLISH=PUSHED"
