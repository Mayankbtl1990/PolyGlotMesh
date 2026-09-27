# Native Image Investigation Results

Fill this document with actual results from the team's machine.

Do not mark an item PASS without running it.

## Environment

- Date:
- Member:
- OS and architecture:
- CPU:
- RAM:
- java -version:
- mvn -version:
- native-image --version:
- Polyglot dependency version:
- Git commit tested:

## Build command

    cd backend
    mvn -Pnative-prototype -DskipTests clean package

## Results

| Check | Status | Evidence |
| --- | --- | --- |
| JVM regression tests | NOT RUN | |
| Spring AOT processing | NOT RUN | |
| Native compilation | NOT RUN | |
| Native startup | NOT RUN | |
| Health endpoint | NOT RUN | |
| Diagnostics reports NATIVE_IMAGE | NOT RUN | |
| Python execution | NOT RUN | |
| JavaScript execution | NOT RUN | |
| Pricing audit | NOT RUN | |
| Native cancellation | NOT RUN | |
| Native filesystem restrictions | NOT RUN | |

Allowed statuses:
- PASS
- FAIL
- BLOCKED
- NOT RUN

## First blocking error

Paste a short relevant error excerpt, not an entire multi-megabyte build log.

## Analysis

- Which stage failed?
- Is the issue documented for this exact toolchain/runtime combination?
- What change was attempted?
- Did the change preserve JVM compatibility?

## Decision

Choose and justify one:

- Continue with a verified native executable.
- Continue native investigation while using the JVM deployment.
- Defer embedded native deployment due to a documented compatibility blocker.

## Performance claims

Do not claim improved startup or execution speed without measurement.

Record cold startup separately from guest execution.
Record Python and JavaScript separately.
Use the same scripts, hardware, limits, and measurement boundaries.

A successful build is not a performance measurement.