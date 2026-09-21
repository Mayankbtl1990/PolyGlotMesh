# PolyglotMesh API — Week 2

This document supersedes the Week 1 error and cancellation limitations.

## POST /api/executions

Request remains:

{
  "language": "python",
  "code": "print('Hello')"
}

Successful response shape is unchanged.

### Guest failure

HTTP 422:

{
  "code": "SCRIPT_ERROR",
  "message": "...",
  "execution": {
    "language": "python",
    "stdout": "...",
    "stderr": "",
    "durationMs": 12.3,
    "outputTruncated": false
  },
  "guestStack": [
    "<module> (script.py:2)"
  ]
}

Guest frames are limited to eight entries.
Host Java stack traces are not returned.

### Evaluation deadline

HTTP 422 with code EXECUTION_TIMEOUT.

Default evaluation deadline: 5000 ms.

Override with:

polyglotmesh.execution.timeout-ms

Allowed configuration range: 100–30000 ms.

The timer covers context.eval, including parsing and initialization occurring
inside evaluation. It excludes queue waiting and context construction.

Cancellation is requested with Context.close(true).
There is no hard CPU quota or guest heap limit.

Captured output available before failure is included when the runtime can
return a structured execution failure.

Server duration includes context setup and, on normal execution/handled guest
failure, teardown. It is not the same measurement as the evaluation deadline.

## POST /api/audits/pricing

Content-Type: application/json
Body: {}

Executes a fixed Python pricing demonstration.

Java allocates a HashMap containing:
- basePrice: 100.0
- discount: 0.15
- finalPrice: 0.0

A controlled ProxyObject exposes these fields to Python.
Only finalPrice is writable.

Expected response values:
- finalPrice: 85.0
- backingMapUpdated: true

Java-to-Python interaction does not use JSON serialization.
The proxy performs interop operations and numeric conversion.
The HTTP response to the frontend still uses JSON.

## GET /api/runtime/capabilities

Returns:
- Executable language identifiers.
- Configured evaluation deadline and its scope.
- Captured output limit.
- Configured guest access restrictions.
- Explicit absence of hard CPU/memory limits and authentication.

This reports configuration, not an exhaustive security certification.

## Deployment boundary

Local development only.
Do not expose this service as a public arbitrary-code execution platform.