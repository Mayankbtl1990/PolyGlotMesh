# PolyglotMesh — Week 1 Setup

## Prerequisites

- GraalVM JDK 21
- Maven 3.9+
- Node.js 20.19+
- Git
- A supported GraalPy platform: use Linux/macOS or Linux through WSL2

Verify Maven is using JDK 21:

    java -version
    mvn -version

## Start the backend

From the repository root:

    cd backend
    mvn spring-boot:run

Backend:
http://127.0.0.1:8080

Health:
http://127.0.0.1:8080/api/health

The first Maven build downloads GraalVM language dependencies.
The first language execution can be substantially slower than later runs.

## Start the frontend

In another terminal:

    cd frontend
    npm ci
    npm run dev

Open:
http://127.0.0.1:5173

Use the Vite development server for the integrated Week 1 demo.
It proxies /api requests to Spring Boot.

The production frontend build is verified, but production hosting and
reverse-proxy configuration are not implemented in Week 1.

## Run checks

Backend:

    cd backend
    mvn clean test

Frontend:

    cd frontend
    npm ci
    npm test
    npm run build

## Browser smoke test

1. Open the application.
2. Confirm Monaco loads without worker errors.
3. Run the default Python script.
4. Confirm stdout contains: Final price: 85.0
5. Switch to JavaScript.
6. Run the default JavaScript script.
7. Confirm stdout contains: Validation passed: 85
8. Switch to Java.
9. Confirm Java execution is disabled.
10. Return to Python and run: print(1 / 0)
11. Confirm an error message appears without crashing the page.
12. Run a valid script again and confirm recovery.
13. Change tabs and confirm each script keeps its own content.
14. Refresh and confirm scripts reset: persistence is not implemented yet.

## Important limitations

- Local trusted-script prototype, not a hardened public sandbox.
- No authentication.
- No hard CPU or guest memory isolation.
- No infinite-loop cancellation.
- Closing the browser does not guarantee guest execution stops.
- Never test infinite loops during Week 1.
- Captured stdout and stderr are capped at 64 KiB each.
- Output truncation does not terminate execution.
- Guest errors return a concise message, not a full structured traceback.
- Output produced before a failed execution is not returned.
- Java is editor-only.
- Ruby is not implemented.
- Scripts are held in browser memory and disappear on refresh.
- Execution time includes context creation, evaluation, and closure.
- No claim of guaranteed sub-millisecond performance.

## Troubleshooting

### No Python language installed

Confirm the python-community dependency is present and uses the same
version as the Polyglot API. Check that your OS/architecture is supported.

### Compilation reports an unsupported Java release

Maven is using the wrong JDK. Fix JAVA_HOME and check mvn -version.

### Browser cannot reach backend

Start Spring Boot on port 8080 and use the frontend Vite server on 5173.

### First execution is slow

Language initialization and compilation may occur on first use.
Measure cold and warm execution separately.

### Backend is stuck after an accidental infinite loop

Stop and restart the backend JVM. Request cancellation is not implemented.

### Monaco fails to load

Run npm ci, restart Vite, and inspect the browser console for worker errors.
Monaco is bundled locally; the implementation does not require its default CDN.