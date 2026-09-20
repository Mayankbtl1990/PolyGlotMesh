import { useEffect, useState } from "react";
import { getRuntimeCapabilities } from "../lib/api";

export default function RuntimePolicyPanel() {
  const [capabilities, setCapabilities] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    getRuntimeCapabilities()
      .then((response) => {
        if (active) {
          setCapabilities(response);
        }
      })
      .catch((failure) => {
        if (active) {
          setError(failure.message || "Cannot load runtime policy.");
        }
      });

    return () => {
      active = false;
    };
  }, []);

  return (
    <details className="panel policy-panel">
      <summary>Runtime policy and limitations</summary>

      {error && <p role="alert">{error}</p>}

      {!capabilities && !error && (
        <p className="muted">Loading configured policy…</p>
      )}

      {capabilities && (
        <>
          <p>
            Evaluation deadline:{" "}
            <strong>{capabilities.evaluationTimeoutMs} ms</strong>
          </p>

          <p className="muted">
            Scope: {capabilities.timeoutScope}.
          </p>

          <p className="muted">
            Output capture: {capabilities.capturedBytesPerStream / 1024} KiB
            per stream.
          </p>

          <ul>
            {capabilities.restrictions.map((restriction) => (
              <li key={restriction}>{restriction}</li>
            ))}
          </ul>

          <p className="warning">
            Hard memory limit: {capabilities.hardMemoryLimit ? "Yes" : "No"}.
            {" "}Hard CPU quota: {capabilities.hardCpuQuota ? "Yes" : "No"}.
            {" "}Authentication: {capabilities.authenticated ? "Yes" : "No"}.
          </p>

          <p className="warning">
            These are configured restrictions, not proof of complete isolation.
            Do not expose this development server to untrusted users.
          </p>
        </>
      )}
    </details>
  );
}