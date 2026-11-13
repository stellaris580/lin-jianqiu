# Public Release Pipeline
Whitelist source of truth: `PUBLIC_SOURCE_ALLOWLIST.txt`.

Flow:

```
public working tree
  -> whitelist check
  -> forbidden extension/secret scan
  -> Java 17 build + DEMO_RESULT=PASS
  -> regenerate SOURCE_MANIFEST.sha256
  -> dry-run or explicit -Publish
  -> optional commit/tag/push
  -> GitHub Actions CI
  -> tag-triggered Release workflow (contents: write only in release job)
  -> remote verification
```

Dry run:

```powershell
pwsh scripts/public-release.ps1 -DryRun
```

Publish only with an independent audit evidence file containing exact lines `PRIVACY_AUDIT=PASS` and `INDEPENDENT_AUDIT=PASS`:

```powershell
pwsh scripts/public-release.ps1 -Publish -Message "docs: ..." -Tag v0.1.1-alpha
```

Safety: forbidden files fail closed; missing audit or failing build blocks publishing; tags are never force-moved; existing tags are rejected. CI is read-only. Release has `contents: write` only in the release job. No private Android source, personal data, model credentials, QQ, or phone code is exported.
