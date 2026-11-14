# Unattended Conditional Publish

The weekly DSH check runs `scripts/unattended-publish.ps1`.

Logic:

```
detect public-safe change
  -> dry-run: exact allowlist, secret scan, Java 17 build, DEMO_RESULT=PASS, manifest
  -> independent audit attestation bound to snapshot + manifest SHA-256
  -> NO_PUBLIC_UPDATE_NEEDED if there is no material change
  -> -Publish commit/push main with independent evidence
  -> optional Version argument creates v<version>-alpha tag
  -> GitHub Actions CI
  -> tag-triggered Release workflow (no per-release human approval)
  -> remote verification
```

Required evidence fields are documented in `VERIFIED_RELEASE_ATTESTATION.md`. Missing, stale or hash-mismatched evidence causes a non-zero exit and nothing is committed or pushed. The attestation is not cryptographically signed by default: it binds a declared auditor identity and exact hashes, but a malicious local operator could fabricate it. Trust the auditing agent and isolate credentials; do not claim absolute proof.

Scope expansion is enforced locally: if `PUBLIC_SOURCE_ALLOWLIST.txt` changed relative to HEAD, the unattended publisher exits `AUTO_PUBLISH=BLOCKED SCOPE_EXPANSION_REQUIRES_APPROVAL` unless an operator explicitly passes `-ScopeApproved`. Human approval remains required for private Android code, personal memories/persona, QQ/phone credentials, new external services, license changes, or any data outside the public allowlist.

The weekly check is executed by the local DSH scheduler. If DSH is not running, no local file can be read or published; the repository must not claim cloud-side access to the private PC. GitHub Actions verifies pushed commits but cannot see local-only changes.

Tag protection: configure a GitHub Ruleset for `v*-alpha` to restrict tag creation to the trusted maintainer and prevent update/deletion. This is a one-time repository setting; without it, a write collaborator could create a tag that passes CI. Do not claim absolute leak prevention.
