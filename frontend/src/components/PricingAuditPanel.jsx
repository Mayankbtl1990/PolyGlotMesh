import { useRef, useState } from "react";
import { runPricingAudit } from "../lib/api";

export default function PricingAuditPanel() {
  const inFlight = useRef(false);

  const [running, setRunning] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  async function runAudit() {
    if (inFlight.current) {
      return;
    }

    inFlight.current = true;
    setRunning(true);
    setError("");
    setResult(null);

    try {
      setResult(await runPricingAudit());
    } catch (failure) {
      setError(failure.message || "Pricing audit failed.");
    } finally {
      inFlight.current = false;
      setRunning(false);
    }
  }

  return (
    <section className="panel audit-panel" aria-label="Pricing interoperability audit">
      <div className="panel-heading">
        <h2>Java → Python Interoperability Audit</h2>

        <button
          type="button"
          className="secondary-button"
          disabled={running}
          onClick={runAudit}
        >
          {running ? "Auditing…" : "Run pricing audit"}
        </button>
      </div>

      <p className="muted">
        Java creates a HashMap. Python updates finalPrice through a restricted
        map-backed proxy. The browser response still uses JSON.
      </p>

      {error && <p role="alert" className="console-error">{error}</p>}

      {result && (
        <>
          <dl className="audit-grid">
            <div>
              <dt>Base price</dt>
              <dd>{result.basePrice.toFixed(2)}</dd>
            </div>

            <div>
              <dt>Discount</dt>
              <dd>{(result.discount * 100).toFixed(0)}%</dd>
            </div>

            <div>
              <dt>Final price</dt>
              <dd>{result.finalPrice.toFixed(2)}</dd>
            </div>

            <div>
              <dt>Original map updated</dt>
              <dd>{result.backingMapUpdated ? "Yes" : "No"}</dd>
            </div>
          </dl>

          <pre className="console-output">
            {result.execution.stdout}
          </pre>
        </>
      )}
    </section>
  );
}