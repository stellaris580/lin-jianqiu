# Rollback and Emergency Stop

- Never force-push or rewrite public history.
- To stop automation: disable the local scheduled task and revoke publish credentials; archive the repository if a privacy incident is suspected.
- Revert a bad public change with a new commit (`git revert <sha>`), never `reset` on the remote.
- If a secret is leaked: revoke it first, remove it with a reviewed commit, and report privately; history rewriting requires explicit owner approval.
- Existing tags are immutable; publish a corrective version instead.
