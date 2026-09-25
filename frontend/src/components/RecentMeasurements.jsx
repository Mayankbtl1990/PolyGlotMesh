export default function RecentMeasurements({ samples }) {
  const visible = samples.slice(-10).reverse();

  const maximum = Math.max(
    1,
    ...visible.map((sample) => sample.durationMs),
  );

  if (visible.length === 0) {
    return <p className="muted">No recent measurements.</p>;
  }

  return (
    <section aria-label="Recent measurements">
      <h3>Latest measurements</h3>

      <p className="muted">
        Bars are relative to the largest visible sample. Different languages,
        cold starts, failures, and mock delays are not equivalent workloads.
      </p>

      <div className="measurement-table-wrapper">
        <table className="measurement-table">
          <thead>
            <tr>
              <th scope="col">Source</th>
              <th scope="col">Status</th>
              <th scope="col">Duration</th>
            </tr>
          </thead>

          <tbody>
            {visible.map((sample, index) => (
              <tr key={`${sample.recordedAt}-${index}`}>
                <td>
                  {sample.category === "GUEST"
                    ? sample.language
                    : "Mock delay"}
                </td>

                <td>{sample.success ? "Completed" : "Failed"}</td>

                <td>
                  <span>{sample.durationMs.toFixed(2)} ms</span>

                  <div
                    className={`measurement-bar ${
                      sample.category === "GUEST" ? "guest" : "mock"
                    }`}
                    style={{
                      width: `${(sample.durationMs / maximum) * 100}%`,
                    }}
                    aria-hidden="true"
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}