# Public Release Pipeline
Whitelist source of truth: `PUBLIC_SOURCE_ALLOWLIST.txt` (explicit files, no wildcards).

Flow:

```
all worktree files (hidden included)
  -> exact allowlist / forbidden extension / secret scan
  -> Java 17 build + DEMO_RESULT=PASS
  -> regenerate canonical SOURCE_MANIFEST.sha256
  -> source-snapshot SHA256 + manifest SHA256
  -> external independent audit attestation bound to both hashes
  -> optional commit/tag/push
  -> GitHub Actions CI
  -> public-release environment approval
  -> tag-triggered Release (only release job has contents:write)
  -> remote verification
```

Dry run:

```powershell
pwsh scripts/public-release.ps1 -DryRun
```

Publish only with audit evidence stored outside the repo:

```powershell
pwsh scripts/public-release.ps1 -Publish `
  -Message "docs: ..." `
  -Tag v0.1.2-alpha `
  -AuditEvidence D:\path\to\audit-attestation.txt
```

Stale evidence (any byte change after audit) is rejected by snapshot and manifest hashes. Tag releases are additionally gated by the `public-release` GitHub Environment required reviewer; see `docs/RELEASE_SECURITY_GATE.md`.
