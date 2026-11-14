# DSH Schedule Recovery

The weekly conditional-publish schedule is a DSH host-level schedule, not a Windows Scheduled Task.

Verified behavior:

- `schedule_list` shows the active weekly job (Sunday 03:00 Asia/Shanghai).
- The schedule prompt runs a dry-run first and reports `NO_PUBLIC_UPDATE_NEEDED` without creating a commit when the tree is clean.
- A publish requires an external audit attestation; missing/stale evidence blocks push.
- Scope expansion requires explicit `-ScopeApproved`.
- The schedule is persisted by the DSH host; this cannot be verified by the public repository.
- Windows `schtasks` creation was attempted and rejected by the host (`ERROR: The system cannot find the path specified`), so no OS-level task claim is made.
- If DSH is not running, no local sync/publish occurs; the project must not claim cloud-side access to private files.
- Missed runs are not blindly replayed by the scripts; a manual dry-run is required after long downtime.
- Failures exit non-zero and do not auto-retry in a loop.

## Recovery after restart

1. Confirm DSH schedule is listed.
2. Run `scripts/public-release.ps1 -DryRun` manually.
3. If clean, expect `NO_PUBLIC_UPDATE_NEEDED`.
4. If changes exist, obtain a current audit attestation, then run the unattended publisher once.
