# Verified Release Attestation

A publish is allowed only with an external audit evidence file bound to the exact candidate tree.

Required evidence fields (exact lines):

```text
PRIVACY_AUDIT=PASS
INDEPENDENT_AUDIT=PASS
AUDITOR=<independent auditor identity>
AUDITED_AT=<ISO-8601 timestamp>
AUDIT_SCOPE=<files/areas checked>
AUDIT_CHANGES=<summary or count of reviewed changes>
AUDIT_SOURCE_SNAPSHOT_SHA256=<sha256 of the canonical manifest body>
AUDIT_MANIFEST_SHA256=<sha256 of SOURCE_MANIFEST.sha256 bytes>
```

`SOURCE_MANIFEST.sha256` is canonical `<lower-hex>  <path>` lines, LF, no BOM, self-excluded.

`scripts/public-release.ps1` regenerates the manifest immediately before checking the evidence and rejects:

- missing/forged PASS without all fields -> `AUDIT_FIELD_MISSING` / `AUDIT_NOT_PASS`;
- any source change after audit -> `STALE_AUDIT_SNAPSHOT` / `STALE_AUDIT_MANIFEST`;
- unknown auditor, missing scope/changes, bad timestamp -> `AUTO_PUBLISH=BLOCKED`.

The evidence file must be stored **outside** the public worktree. A stale audit can never be reused because both hashes are recomputed from the candidate tree.
