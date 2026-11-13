# Architecture (Public Reference)

```mermaid
flowchart LR
  P[PLAN] --> A{ADJUST needed?}
  A -->|yes| AD[ADJUST before reflection]
  A -->|no| E[EXECUTE]
  AD --> E
  E --> V[VERIFY]
  V --> R[REFLECT based on verified events only]
  R --> C[DAY_COMPLETED]
  S[STOP / budget / idempotency guards] -.-> P
  S -.-> A
  S -.-> E
  S -.-> C
```

The preview shows the safety semantics with fictional data. The Android implementation adds SQLite persistence, WorkManager and ContextBuilder; those are not exported yet.