# Release Security Gate

## One-time owner setup (required before using tag-triggered Releases)

In the GitHub repository:

**Settings -> Environments -> New environment -> `public-release`**

Add **Required reviewers** and select `stellaris580` (or the maintainer). Save.

Without this environment rule, a tag push would create a Release without a human approval. The workflow already requires:

- the tag must be `vX.Y.Z-alpha`;
- the tag target must be exactly current `main` HEAD;
- read-only CI verification must pass first;
- only the release job receives `contents: write`;
- existing releases are never overwritten;
- no tag is moved or force-pushed.

A human reviewer approval is the independent release authorization. This is a one-time repository setting and cannot be enabled by automation without admin permission.

## Failure behavior

Any failed CI step, tag mismatch, environment rejection, or release-creation error blocks the Release. Direct tags are not trusted as approval.
