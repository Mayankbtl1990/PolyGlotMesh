export default function ConsolePanel({
  stdout = "",
  stderr = "",
  error = "",
  running = false,
  durationMs = null,
  outputTruncated = false,
}) {
  return (
    <section className="panel console-panel" aria-label="Execution console">
      <div className="panel-heading">
        <h2>Console Output</h2>
        <span role="status">
          {running ? "Running…" : "Ready"}
        </span>
      </div>

      {durationMs !== null && (
        <p className="muted">
          Server execution: {durationMs.toFixed(2)} ms
        </p>
      )}

      {error && (
        <pre className="console-error" role="alert">
          {error}
        </pre>
      )}

      {stderr && (
        <>
          <h3>Standard error</h3>
          <pre className="console-error">{stderr}</pre>
        </>
      )}

      <h3>Standard output</h3>

      <pre className="console-output">
        {stdout || (running ? "Waiting for output…" : "No output yet.")}
      </pre>

      {outputTruncated && (
        <p className="warning">
          Output was truncated to the server capture limit.
        </p>
      )}
    </section>
  );
}