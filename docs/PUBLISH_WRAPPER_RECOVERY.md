# Publish Wrapper Recovery

Observed failure class: `git push` could succeed while PowerShell's child-process wrapper aborted on stderr, leaving `main` pushed and a tag absent.

## Fixes

- `public-release.ps1` captures native command exit codes under `ErrorActionPreference = Continue`; stderr text is printed but no longer terminates before the exit code is read.
- Main push, tag create, and tag push each have distinct states:
  - `PUBLISH_STATE=MAIN_PUSHED`
  - `PUBLISH_STATE=TAG_CREATED`
  - `PUBLISH_STATE=TAG_PUSHED`
- Partial success is explicit: `PARTIAL_PUBLISH MAIN_PUSHED=true TAG_CREATED=false` or `... TAG_PUSHED=false`.
- Tag format is validated before any commit or main push.
- `-RecoverTag -Tag vX.Y.Z-alpha` is a recovery-only path: it verifies remote `main` equals local HEAD, verifies the tag is absent locally and remotely, then creates/pushes only the tag. It does not repeat the main push or create a new commit. If the local tag was already created but its push failed, recovery verifies that tag points at HEAD and pushes it without recreating it.
- Existing tags are rejected locally and remotely; no tag move/force-push.
- Recovery still requires the same audit evidence bound to the current manifest/snapshot; no privacy gate is skipped.

## Manual recovery

```powershell
pwsh scripts/public-release.ps1 -Publish -RecoverTag -Tag v0.1.3-alpha -AuditEvidence D:\path\to\evidence.txt
```

The command exits non-zero on mismatch; a successful recovery prints `PARTIAL_RECOVERY=PASS`.
