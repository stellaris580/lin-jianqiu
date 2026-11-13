# Private Data Exclusion Audit
Excluded categories: API keys/tokens/cookies, QQ/email accounts, real contacts/chat logs, personal persona and memories, device serials, absolute host paths, Android logs/databases/screenshots, internal evidence, APK/binary artifacts, internal Git history.
Export method: file whitelist copy into a new directory; no `git clone`, no internal `.git` copied, no full worktree sync; a new sanitized local `.git` was initialized for the public candidate.
Scan: see scan result appended by maintainer before release. Manual review required.
## Automated scan result
SECRET_SCAN=PASS (NO_MATCH in source/docs after excluding manifest hashes and demo numeric output)
PATTERNS: credential prefixes, authorization headers, secret/config assignments, device identifiers, absolute host paths, internal package identifiers, email addresses. Actual sensitive literals are deliberately not reproduced in this audit.
LIMITATION: pattern scan + manual whitelist review, not a proof of zero secrets.

## Source manifest
See SOURCE_MANIFEST.sha256 (all exported files except the manifest itself and build artifacts).