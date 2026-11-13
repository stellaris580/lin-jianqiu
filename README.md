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

## 中文说明
本项目是实验性数字生命/Agent 系统的公开研究预览。当前仅包含虚构数据的参考演示，不包含真实模型、QQ、手机控制、个人记忆或生产凭据。详细限制见 `KNOWN_LIMITATIONS.md`。