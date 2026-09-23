# Week 2 — Mid-Project Review

## Contribution checklist

| Day | Mansi | Balaji | Mayank | Bhanu |
| --- | --- | --- | --- | --- |
| 1 | Context policy | Deadline configuration | Resizable workspace | Backend status |
| 2 | Guest cancellation | Detailed error API | Error console UI | Error-aware client |
| 3 | Pricing bindings | Audit API | Audit integration and shortcut | Audit panel |
| 4 | Cancellation and file tests | Capability API | Policy panel | Timeout UI tests |
| 5 | Binding policy tests | Audit HTTP tests and docs | Workspace tests | Audit UI tests and review |

Minimum:
- Five meaningful commits per member.
- Twenty meaningful commits across the team.

## Automated verification

On Linux/WSL:

Backend:

    cd backend
    timeout 300s mvn test

Frontend:

    cd frontend
    npm ci
    npm test
    npm run build

A process-watchdog timeout is a failed test run, not a successful cancellation
test.

## Demo prerequisites

- Automated cancellation tests pass on the demo machine.
- Backend remains bound to localhost.
- No arbitrary third-party or hostile scripts are used.
- Python and JavaScript have each executed a small warmup script.

## Demo sequence

### 1. Basic execution

Python:

    print("Week 2 Python works")

JavaScript:

    console.log("Week 2 JavaScript works");

### 2. Error and partial output

JavaScript:

    console.log("before failure");
    throw new Error("Controlled demo failure");

Confirm:
- Partial stdout is visible.
- Error code is SCRIPT_ERROR.
- Guest frames can be expanded.

### 3. Controlled deadline demonstration

Only after the automated cancellation tests pass:

Python:

    while True:
        pass

Confirm:
- EXECUTION_TIMEOUT appears.
- The Run button becomes available again.
- A subsequent print script succeeds.

If the backend does not recover, stop/restart the JVM and record the defect.
Do not claim guaranteed cancellation.

### 4. Java HashMap interoperability

Click Run pricing audit.

Confirm:
- Base price: 100.00
- Discount: 15%
- Final price: 85.00
- Original map updated: Yes

Explain:
- Python receives a controlled view backed by the original Java map.
- Only finalPrice is writable.
- Java/Python communication does not serialize the map to JSON.
- Numeric conversion and interoperability still have costs.
- The browser HTTP response is JSON.

### 5. UI controls

- Resize the editor using the mouse.
- Resize using keyboard arrows.
- Run with Ctrl+Enter or Command+Enter.
- Confirm Java execution remains disabled.

### 6. Runtime limitations

Open Runtime policy and limitations.

Explain:
- Configured guest restrictions.
- Evaluation-only deadline scope.
- No hard CPU quota.
- No hard guest memory limit.
- No authentication.
- No public deployment support.

## Evidence

Fill in after verification:

- Backend test output:
- Frontend test output:
- Browser screenshots/recording:
- Cancellation test result:
- Pricing audit result:
- Pull request links:
- Known defects: