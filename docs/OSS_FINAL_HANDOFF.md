# OSS Final Handoff

The public release system is in maintenance mode.

Current state (at handoff):

- Public repo: https://github.com/stellaris580/lin-jianqiu
- 50 controlled files, Apache-2.0, Java 17 offline demo.
- Automated main push and tag-triggered Release are operational.
- Latest release: v0.1.2-alpha (see release page).
- Audit attestation binds exact snapshot/manifest hashes; stale/forged-hash/scope changes fail closed.
- Routine updates do not require human approval; scope expansion does.
- One-time external hardening remains: configure the `v*-alpha` tag Ruleset (see TAG_RULESET_COMPATIBILITY.md).
- DSH schedule is host-level; Windows Task Scheduler is unavailable on this host.

Do not extend release infrastructure further in this phase. Next engineering priorities are Android Worker concurrency/UNKNOWN receipt device tests, phone Agent integration, long-term memory/personality continuity, and controlled real-model cross-day research. No new paid model calls, real QQ, or phone external actions are authorized by this handoff.
