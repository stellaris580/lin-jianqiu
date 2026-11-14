# Audit Trust Boundary

Separate properties:

| Property | Current mechanism | Strength |
|---|---|---|
| Content integrity | SHA-256 of every public file + canonical SOURCE_MANIFEST + snapshot hash | Strong for accidental/stale changes |
| Audit subject identity | `AUDITOR=e8-qa` text field in attestation | Declared only; not cryptographically verified |
| Independent approval | Separate audit agent reads the exact tree and writes evidence outside the repo | Process separation, not a signature |
| Publisher permission | SSH key acting as repository admin | Strong for access control, but same OS user can read the evidence path |

The attestation file itself is unsigned unless an external signer is added. A local process with the same filesystem permissions as the publisher could fabricate both the audit text and its hashes. Therefore:

- Content integrity is enforced and stale/forged *hash bindings* are rejected.
- The auditor identity is a declared trust anchor, not a cryptographic proof.
- The publisher and auditor SHOULD run in separate trust contexts; on one workstation this is only process-level separation.
- Routine updates do not need a human approval, but scope expansion still requires explicit `-ScopeApproved` plus human review.

Minimal future hardening (not added now): sign attestation with a dedicated `ssh-keygen -Y sign` audit key, store its public key in the repo, and verify the signature in the publish script and/or CI. Avoid adding long-lived shared secrets.
