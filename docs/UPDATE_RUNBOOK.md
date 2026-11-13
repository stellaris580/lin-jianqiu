# Update and Recovery Runbook

1. Select a stable, public-safe internal result.
2. Export only through the whitelist; never mirror internal Git history.
3. Replace personal data with fictional fixtures.
4. Run `pwsh scripts/public-release.ps1 -DryRun`; require all gates PASS.
5. Obtain independent privacy/security review of the exact tree to be pushed.
6. Commit and push `main`; CI must pass.
7. Create a new immutable tag only from the verified commit; never move or force tags.
8. The tag triggers the release workflow; verify the public Release by API or browser.

Weekly check: run the dry-run and compare internal public-safe changes; report `NO_PUBLIC_UPDATE_NEEDED` when no material change. This local Agent cannot see new files when it is not running; there is no cloud-side access to the private PC.

Fail closed: if any of whitelist, privacy, license, build, test or scope evidence is missing/UNKNOWN, the pipeline throws `PUBLISH_GATE=BLOCKED` (or `AUTO_PUBLISH=BLOCKED MISSING_INDEPENDENT_AUDIT` / `AUDIT_NOT_PASS`); nothing is pushed.
