# Metrics Semantics

## Guest measurements

Source: POST /api/executions.

Measured interval:
GuestRuntime.execute(...) entry to exit.

Includes:
- Context construction.
- Evaluation.
- Synchronous context cleanup.
- Successful and failed runtime calls.

Excludes:
- HTTP network transit.
- WebFlux scheduler queue wait.
- Request validation failures.
- Scheduler rejection before execution starts.
- Pricing audit calls.

A hung runtime call cannot contribute its final duration until it exits.

## Aggregation

Counters and arithmetic means cover the current backend process lifetime.

Python, JavaScript, cold starts, warm runs, successes, and failures are mixed
in the aggregate guest mean.

Inspect recent samples for language and success status.

Only 50 recent samples are retained.
Restarting the process clears all metrics.

No source code or console output is recorded.

## Mock baseline

The mock endpoint schedules an artificial 50 ms delay.

Observed duration includes delay scheduling overhead.

It does not:
- Execute the submitted script.
- Call another service.
- Measure browser-to-server latency.
- Establish a real REST-versus-polyglot speedup.

Do not present the ratio of these means as a benchmark result.

## Real benchmark follow-up

For a valid comparison:
- Execute the same workload in both architectures.
- Control hardware, concurrency, payload size, and dependencies.
- Separate cold starts from warmed runs.
- Measure distributions, not just means.
- Report end-to-end and server-only timing separately.