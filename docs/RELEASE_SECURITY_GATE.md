# Release Security Gate

Releases are automated. There is **no per-release human approval button**.

Controls that remain:

- Tag must match `vX.Y.Z-alpha`.
- Tag target must be exactly current `main` HEAD.
- Read-only CI must pass first.
- Only the release job gets `contents: write`.
- Existing releases are never overwritten; tags are never moved or force-pushed.
- CI enforces the explicit source allowlist, canonical `SOURCE_MANIFEST.sha256`, secret/forbidden-file checks and `DEMO_RESULT=PASS`.

## Required one-time external control: tag Ruleset

Configure a GitHub Ruleset for `refs/tags/v*-alpha` that restricts tag creation/update/deletion to the trusted maintainer. This is not a per-release approval; it prevents an arbitrary write collaborator from creating a release tag. Until this Ruleset exists, tag protection is **not** enforced by the repository tree and this residual risk must be reported. A GitHub Environment with required reviewers is optional and is NOT required for routine releases.

## Scope expansion

Routine docs/demo/tests may publish automatically after an independent audit attestation. Adding a new file type or broadening the public allowlist is a scope expansion: the unattended publisher refuses to proceed unless the operator explicitly passes `-ScopeApproved`. Human approval is still required for private Android code, personal memories/persona, QQ/phone credentials, new external services, license changes, or anything outside the public allowlist.

## Attestation limitation

The local attestation binds an independent auditor's declared identity to exact snapshot/manifest hashes, but it is not a cryptographic signature by default. Do not describe it as an absolute proof; use a trusted auditing agent and isolate the publish credentials.
