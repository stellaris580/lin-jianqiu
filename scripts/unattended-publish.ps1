param(
  [Parameter(Mandatory=$false)][string]$AuditEvidence = "",
  [Parameter(Mandatory=$false)][string]$Version = "",
  [Parameter(Mandatory=$false)][string]$Message = "",
  [switch]$ScopeApproved
)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root
if (-not $Message) { $Message = "docs: automated public update" }
$allowChanged = @(git diff --name-only HEAD -- PUBLIC_SOURCE_ALLOWLIST.txt).Count -gt 0
if ($allowChanged -and -not $ScopeApproved) {
  Write-Output "AUTO_PUBLISH=BLOCKED SCOPE_EXPANSION_REQUIRES_APPROVAL"
  exit 3
}
$ErrorActionPreference = "Continue"
$dry = & powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root "scripts/public-release.ps1") -DryRun 2>&1
$dryCode = $LASTEXITCODE
$ErrorActionPreference = "Stop"
$dry | Write-Output
if ($dryCode -ne 0) { throw "AUTO_PUBLISH=BLOCKED DRY_RUN_FAILED" }
$dirty = @(git status --porcelain)
if ($dirty.Count -eq 0 -and -not $Version) {
  Write-Output "NO_PUBLIC_UPDATE_NEEDED"
  exit 0
}
if (-not $AuditEvidence -or -not (Test-Path $AuditEvidence)) {
  Write-Output "AUTO_PUBLISH=BLOCKED NO_INDEPENDENT_AUDIT"
  exit 2
}
$args = @("-Publish", "-Message", $Message, "-AuditEvidence", $AuditEvidence)
if ($Version) { $args += @("-Tag", "v$Version-alpha") }
$ErrorActionPreference = "Continue"
$publish = & powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root "scripts/public-release.ps1") @args 2>&1
$publishCode = $LASTEXITCODE
$ErrorActionPreference = "Stop"
$publish | Write-Output
if ($publishCode -ne 0) { throw "AUTO_PUBLISH=BLOCKED PUBLISH_FAILED" }
Write-Output "AUTO_PUBLISH=AUTHORIZED_AND_GATED"
