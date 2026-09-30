# Week 3 Review

## Contributions

| Day | Mansi | Balaji | Mayank | Bhanu |
| --- | --- | --- | --- | --- |
| 1 | Runtime diagnostics | Metrics store | Metric cards | Observability client |
| 2 | Diagnostics API | Execution instrumentation | Dashboard integration | Metrics polling |
| 3 | Native build profile | Mock delay API | Measurement history | Diagnostics panel |
| 4 | Native smoke script | Metrics tests | Diagnostics integration | Polling tests |
| 5 | Native evidence guide | HTTP tests and metrics docs | History tests | Dashboard tests and review |

Minimum: five meaningful commits per member, twenty across the team.

## Automated checks

Backend, Linux/WSL:

    cd backend
    timeout 300s mvn clean test

Frontend:

    cd frontend
    npm ci
    npm test
    npm run build

Native build attempt, only on a suitable machine:

    cd backend
    mvn -Pnative-prototype -DskipTests clean package

Native runtime checks, only if a native executable exists and is running:

    python3 scripts/native_smoke.py

## Browser demonstration

1. Start the JVM application and frontend.
2. Run a Python script.
3. Run a JavaScript script.
4. Confirm guest metrics update.
5. Run a controlled script error.
6. Confirm the failure count updates.
7. Sample the mock REST delay.
8. Confirm the mock metric and recent history update.
9. Explain that the mock is not an equivalent-workload benchmark.
10. Open runtime diagnostics.
11. Show the actual execution mode.
12. Confirm Week 2 pricing audit still works.

## Native review

Show actual evidence from docs/native-investigation-results.md.

Acceptable honest outcomes:
- Verified native prototype.
- Partially working native prototype with documented blockers.
- Native build blocked; JVM implementation remains working.

Do not describe an untested native executable as production-ready.

## Evidence

- JVM backend test result:
- Frontend test result:
- Browser screenshots:
- Native toolchain details:
- Native build result:
- Native smoke result:
- Known blockers:
- Pull request links: