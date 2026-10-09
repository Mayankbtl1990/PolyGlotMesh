# PolyglotMesh — Setup & System Guide

## Week 1 Setup

### Prerequisites

- GraalVM JDK 21
- Maven 3.9+
- Node.js 20.19+
- Git
- A supported GraalPy platform: use Linux/macOS or Linux through WSL2

Verify Maven is using JDK 21:

```bash
java -version
mvn -version

```

## Start the backend

From the repository root:

```bash
cd backend
mvn spring-boot:run

```

Backend:
http://127.0.0.1:8080

Health:
http://127.0.0.1:8080/api/health

The first Maven build downloads GraalVM language dependencies.
The first language execution can be substantially slower than later runs.

## Start the frontend

In another terminal:

```bash
cd frontend
npm ci
npm run dev

```

Open:
http://127.0.0.1:5173

Use the Vite development server for the integrated Week 1 demo.
It proxies /api requests to Spring Boot.

The production frontend build is verified, but production hosting and
reverse-proxy configuration are not implemented in Week 1.

## Run checks

Backend:

```bash
cd backend
mvn clean test

```

Frontend:

```bash
cd frontend
npm ci
npm test
npm run build

```

## Browser smoke test

1. Open the application.
2. Confirm Monaco loads without worker errors.
3. Run the default Python script.
4. Confirm stdout contains: `Final price: 85.0`
5. Switch to JavaScript.
6. Run the default JavaScript script.
7. Confirm stdout contains: `Validation passed: 85`
8. Switch to Java.
9. Confirm Java execution is disabled.
10. Return to Python and run: `print(1 / 0)`
11. Confirm an error message appears without crashing the page.
12. Run a valid script again and confirm recovery.
13. Change tabs and confirm each script keeps its own content.
14. Refresh and confirm scripts reset: persistence is not implemented yet.

## Important limitations

* Local trusted-script prototype, not a hardened public sandbox.
* No authentication.
* No hard CPU or guest memory isolation.
* No infinite-loop cancellation.
* Closing the browser does not guarantee guest execution stops.
* Never test infinite loops during Week 1.
* Captured stdout and stderr are capped at 64 KiB each.
* Output truncation does not terminate execution.
* Guest errors return a concise message, not a full structured traceback.
* Output produced before a failed execution is not returned.
* Java is editor-only.
* Ruby is not implemented.
* Scripts are held in browser memory and disappear on refresh.
* Execution time includes context creation, evaluation, and closure.
* No claim of guaranteed sub-millisecond performance.

## Troubleshooting

### No Python language installed

Confirm the python-community dependency is present and uses the same
version as the Polyglot API. Check that your OS/architecture is supported.

### Compilation reports an unsupported Java release

Maven is using the wrong JDK. Fix JAVA_HOME and check `mvn -version`.

### Browser cannot reach backend

Start Spring Boot on port 8080 and use the frontend Vite server on 5173.

### First execution is slow

Language initialization and compilation may occur on first use.
Measure cold and warm execution separately.

### Backend is stuck after an accidental infinite loop

Stop and restart the backend JVM. Request cancellation is not implemented.

### Monaco fails to load

Run `npm ci`, restart Vite, and inspect the browser console for worker errors.
Monaco is bundled locally; the implementation does not require its default CDN.

## Week 2 update

For current cancellation, error handling, and interoperability behavior, see:

* docs/api-week-2.md
* docs/progress/week-2.md

These supersede the Week 1 statements that cancellation and partial
failure output are not implemented.

Week 2 adds best-effort evaluation cancellation, not hard resource isolation.

Before demonstrating controlled infinite-loop cancellation, run the automated
cancellation tests under a process-level test watchdog.

Persistence, arbitrary Java execution, hard guest memory limits, and public
deployment support remain unimplemented.

## Week 3 update

New endpoints:

* GET /api/metrics
* GET /api/runtime/diagnostics
* POST /api/benchmarks/mock-rest

The dashboard refreshes metrics approximately every three seconds.

See:

* docs/metrics.md
* docs/native-image.md
* docs/native-investigation-results.md
* docs/progress/week-3.md

Native Image support is experimental until validated on the chosen toolchain.
The normal JVM startup command remains the supported development baseline.

---

## Week 4 — Persistent scripts and mock product data

### New endpoints

* GET /api/scripts
* POST /api/scripts
* GET /api/scripts/{id}
* PUT /api/scripts/{id}
* GET /api/data/products
* POST /api/data/execute

### Database

Scripts are stored in file-backed H2.

Default JDBC URL:
`jdbc:h2:file:./data/polyglotmesh`

The path is relative to the backend process working directory.

Always start the backend from the backend directory for the normal demo:

```bash
cd backend
mvn spring-boot:run

```

Do not run two backend processes against the same database file.

The local data directory is ignored by Git.

Tests use separate in-memory database URLs.
HTTP persistence tests do not replace the manual restart-persistence check.

### Drafts versus saved scripts

Editor drafts remain in browser memory.
Use Save as new or Update loaded script to persist content.

Refreshing the browser discards unsaved drafts.
Saved scripts remain available from the library.

Only one language tab is saved per library entry.

### Mock product binding

The product catalog is Java in-memory data, not MongoDB.

Use the data panel's Run current script with product button for code that
references pricing.

The main Run button does not inject pricing bindings.

### Final review

See:

* docs/architecture.md
* docs/progress/week-4.md

### Remaining limitations

* No authentication or per-user ownership.
* No hard CPU quota or guest memory limit.
* No script revision history or conflict resolution.
* No pagination or storage quotas for the small local script library.
* No real MongoDB connection.
* No arbitrary Java execution.
* No Ruby support.
* No guaranteed native compatibility.
* No production frontend hosting configuration.
* No hardened public deployment.
Do not store secrets or production credentials in saved scripts.