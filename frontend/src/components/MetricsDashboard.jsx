import { useRef, useState } from "react";

import { useMetrics } from "../hooks/useMetrics";
import { runMockRestBaseline } from "../lib/api";
import MetricCards from "./MetricCards";
import RecentMeasurements from "./RecentMeasurements";

export default function MetricsDashboard() {
  const { metrics, error } = useMetrics();

  const inFlight = useRef(false);
  const [runningMock, setRunningMock] = useState(false);
  const [mockError, setMockError] = useState("");

  async function runBaseline() {
    if (inFlight.current) {
      return;
    }

    inFlight.current = true;
    setRunningMock(true);
    setMockError("");

    try {
      await runMockRestBaseline();
    } catch (failure) {
      setMockError(failure.message || "Mock baseline failed.");
    } finally {
      inFlight.current = false;
      setRunningMock(false);
    }
  }

  return (
    <section className="panel metrics-panel" aria-label="Performance metrics">
      <div className="panel-heading">
        <h2>Performance Measurements</h2>

        <button
          type="button"
          className="secondary-button"
          disabled={runningMock}
          onClick={runBaseline}
        >
          {runningMock ? "Measuring…" : "Sample mock REST delay"}
        </button>
      </div>

      <p className="muted">
        Refreshes approximately every 3 seconds. Values reset when the backend
        restarts.
      </p>

      <p className="warning">
        The mock baseline adds an artificial 50 ms delay. It does not execute
        an equivalent script or measure a real remote service.
      </p>

      {error && (
        <p role="alert" className="console-error">
          {error} Previously loaded metrics may be stale.
        </p>
      )}

      {mockError && (
        <p role="alert" className="console-error">{mockError}</p>
      )}

      {!metrics && !error && (
        <p role="status">Loading metrics…</p>
      )}

      {metrics && (
        <>
            <MetricCards metrics={metrics} />
            <RecentMeasurements samples={metrics.recent} />
        </>
       )}
    </section>
  );
}