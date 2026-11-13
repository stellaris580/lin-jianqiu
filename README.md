# Lin Jianqiu Life Loop (Public Research Preview)

> Experimental digital-life and agent research. **Not** a finished general autonomous AI.
> This preview is a reference demo, not the private production app.

This repository demonstrates safety-first life-loop semantics with fictional data:
- deterministic plan / adjust / execute / verify / reflect / day-complete transitions
- refusal of illegal transitions (post-day execution, duplicate completion, unverified reflection)
- budget reservation, call limits, UNKNOWN-usage blocking, duplicate-settlement rejection
- receipt UNKNOWN retained without blind replay

## What this is not
- No real DeepSeek calls, no QQ, no phone control, no network, no personal memory.
- It does **not** prove real-model three-day life, personality continuity, or external-action safety.
- The private Android implementation is not exported in this revision.

## Run
Java 17 only, no third-party dependencies:
```bash
javac -encoding UTF-8 -d build/classes src/main/java/org/example/life/LifeLoopDemo.java
java -cp build/classes org.example.life.LifeLoopDemo
```
Expected final line: `DEMO_RESULT=PASS`.

## Project origin and goal
This is a public research slice of an experimental digital-life project: persistent memory, autonomous planning, reflection, and safe agent execution. The public code demonstrates safety semantics only; the private Android Agent/QQ/reality-query system is not included.

## What is implemented here
- Runnable Java 17 reference simulation (fictional data, offline).
- Lifecycle and negative-transition checks.
- Budget, UNKNOWN-usage, duplicate-settlement and receipt idempotency demonstrations.
- CI verification and an auditable release pipeline (`docs/PUBLIC_RELEASE_PIPELINE.md`).

## Architecture and reproducibility
See `ARCHITECTURE.md`, `docs/QUICKSTART.md`, `docs/EXPERIMENTS.md`, and run the one-line demo above. No API key, account, network or database is required.

## Community and research questions
Issues are enabled. Discussions may be enabled by the maintainer in repository settings. Please do not post secrets or private data.
Questions we especially want feedback on:
1. How should long-term memory be corrected when it is wrong?
2. How should personality continuity be defined and tested?
3. How can autonomous planning remain grounded in verified facts?
4. How should an external action with UNKNOWN outcome recover safely?
5. How can reflection become continuous learning rather than a summary?
6. What verifiable boundary best balances autonomy and human control?

## Known limitations
See `KNOWN_LIMITATIONS.md` and `PRIVACY_AND_SECURITY.md`. This is not a production system and makes no claim of real-world autonomy or external-action safety.

## 中文说明
本项目是实验性数字生命/Agent 系统的公开研究预览。当前仅包含虚构数据的参考演示，不包含真实模型、QQ、手机控制、个人记忆或生产凭据。详细限制见 `KNOWN_LIMITATIONS.md`。
