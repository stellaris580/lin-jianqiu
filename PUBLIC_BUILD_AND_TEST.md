# Public Build and Test
Requirements: JDK 17; no network/dependencies.
```bash
javac -encoding UTF-8 -d build/classes src/main/java/org/example/life/LifeLoopDemo.java
java -cp build/classes org.example.life.LifeLoopDemo
```
Verified from a copied fresh directory; expected `DEMO_RESULT=PASS`. Build output is not committed.