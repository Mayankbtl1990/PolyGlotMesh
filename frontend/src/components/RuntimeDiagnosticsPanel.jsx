import { useEffect, useState } from "react";
import { getRuntimeDiagnostics } from "../lib/api";

export default function RuntimeDiagnosticsPanel() {
  const [diagnostics, setDiagnostics] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    getRuntimeDiagnostics()
      .then((response) => {
        if (active) {
          setDiagnostics(response);
        }
      })
      .catch((failure) => {
        if (active) {
          setError(failure.message || "Cannot load diagnostics.");
        }
      });

    return () => {
      active = false;
    };
  }, []);

  return (
    <details className="panel policy-panel">
      <summary>Runtime diagnostics</summary>

      {error && <p role="alert">{error}</p>}

      {!diagnostics && !error && (
        <p role="status">Loading runtime diagnostics…</p>
      )}

      {diagnostics && (
        <>
          <dl className="audit-grid">
            <div>
              <dt>Execution mode</dt>
              <dd>{diagnostics.executionMode}</dd>
            </div>

            <div>
              <dt>Java version</dt>
              <dd>{diagnostics.javaVersion}</dd>
            </div>

            <div>
              <dt>Polyglot engine</dt>
              <dd>{diagnostics.engineVersion}</dd>
            </div>

            <div>
              <dt>Platform</dt>
              <dd>
                {diagnostics.operatingSystem} / {diagnostics.architecture}
              </dd>
            </div>
          </dl>

          <h3>Discovered languages</h3>

          <ul>
            {diagnostics.installedLanguages.map((language) => (
              <li key={language.id}>
                {language.name} ({language.id}) — {language.version}
              </li>
            ))}
          </ul>

          <p className="warning">
            Language discovery does not prove successful execution.
            Native mode does not imply stronger sandbox isolation.
          </p>
        </>
      )}
    </details>
  );
}