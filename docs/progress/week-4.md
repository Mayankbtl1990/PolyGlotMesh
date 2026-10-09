# Week 4 — Final Project Review

## Team contributions

| Day | Mansi | Balaji | Mayank | Bhanu |
| --- | --- | --- | --- | --- |
| 1 | Mock product catalog | H2 repository | Library view | Library client |
| 2 | Product execution service | Save/load API | Editor library integration | Library workflow |
| 3 | Product service tests | Data binding API | Data panel integration | Data binding panel |
| 4 | Data HTTP tests | Persistence HTTP tests | Library control tests | Library workflow tests |
| 5 | Final architecture | CI workflow | Data tests and UI polish | Setup and final review |

Minimum:
- Five meaningful Week 4 commits per member.
- Twenty meaningful Week 4 commits across the team.
- Eighty meaningful commits across four weeks.

## Start the application

Terminal 1, from repository root:

    cd backend
    mvn spring-boot:run

Terminal 2:

    cd frontend
    npm ci
    npm run dev

Open:
http://127.0.0.1:5173

## Automated checks

Backend, Linux/WSL:

    cd backend
    timeout 900s mvn clean test

Frontend:

    cd frontend
    npm test
    npm run build

The longer backend watchdog accommodates the expanded suite and cold language
initialization. It does not change the guest evaluation deadline.

## Final demo

### 1. Ordinary execution

- Run Python.
- Run JavaScript.
- Show separate tab content.
- Confirm Java cannot execute.

### 2. Guest failure handling

- Print a message and throw a controlled error.
- Show partial output and guest stack details.
- Run a valid script afterward.

### 3. Cancellation

Only after cancellation tests pass on the demo machine:

- Run the controlled Week 2 loop demonstration.
- Show EXECUTION_TIMEOUT.
- Show successful subsequent execution.

Do not claim hard CPU or memory isolation.

### 4. Java/Python interoperability

- Run the fixed pricing audit.
- Explain the controlled HashMap-backed proxy.
- Explain that browser communication still uses JSON.

### 5. Mock data binding

- Choose the Python tab.
- Insert the binding example.
- Select Mechanical Keyboard.
- Run with product.
- Confirm 85.00.

Repeat in JavaScript with Wireless Headphones.
Confirm 180.00.

Explain that the catalog is in-memory and is not MongoDB.

### 6. Persistent script library

- Save a named Python script.
- Change its code.
- Update the saved script with confirmation.
- Refresh the browser.
- Load the saved script.
- Restart the backend from the same working directory.
- Load it again.

Confirm data remains after restart.

Only explicitly saved code persists.
Other open tabs remain browser-memory drafts.

### 7. Metrics and diagnostics

- Show main Run metrics.
- Explain that bound-data executions and audits are excluded.
- Show the synthetic delay baseline.
- Explain why it is not a real equivalent-workload benchmark.
- Show actual JVM/native execution mode.

### 8. Native investigation

Show recorded evidence, not an unsupported claim.

Any Week 3 native result must be revalidated after Week 4 persistence changes.

### 9. CI and contributions

- Show passing GitHub Actions.
- Show member-specific branches and commits.
- Show the contribution schedule.

## Final evidence

- Backend tests:
- Frontend tests:
- Frontend build:
- GitHub Actions run:
- Persistence restart test:
- Data binding result:
- Cancellation result:
- Native build status:
- Demo recording:
- Known limitations: