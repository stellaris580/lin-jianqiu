# Public Source Allowlist
Exported now:
- `src/main/java/org/example/life/LifeLoopDemo.java` (independently written reference demo)
- `src/test/java/...` (placeholder; demo self-tests in main)
- root/community docs, `.github` templates
Explicitly NOT exported in this revision:
- Android production sources (private package tree)
- tests, personas, assets, DBs, APKs, evidence, private docs
- internal `.git` history
Reason: current production implementation has private data coupling and Android dependencies; a clean extraction requires a separate reviewed interface layer.