# Tag Ruleset Compatibility

Verified against GitHub Docs (fetched 2026-11-14):

- Repository rulesets are available for public repositories on GitHub Free.
- Tag rulesets can control tag creation, update and deletion, with `fnmatch` patterns such as `v*-alpha`.
- Bypass can be granted to repository admins, organization owners, write/maintain roles, teams and GitHub Apps.
- Rulesets can be inspected by anyone with read access; creating/editing requires admin permission.

## Recommended one-time setting

Repository -> Settings -> Rulesets -> New tag ruleset:

```text
Name: protect-alpha-tags
Target tags: v*-alpha
Enforcement: Active
Rules: Restrict creations, Restrict updates, Restrict deletions
Bypass list: Repository admin (stellaris580)
```

Effect: collaborators cannot create/move/delete `v*-alpha` tags; the authenticated maintainer/SSH automation, which acts as repository admin, can still publish. This is not a per-release approval and does not block routine automation.

## Residual risk

The SSH publisher authenticates as the admin account. A compromised admin credential/process can still create a tag. A stricter design would use a dedicated GitHub App with only `contents: write` plus a tag ruleset bypass limited to that App; this adds key/installation management and is not required for the current single-maintainer public preview. Do not claim absolute protection against a compromised admin.

Ruleset is an external repository setting; it is not verifiable from the committed tree until configured.
