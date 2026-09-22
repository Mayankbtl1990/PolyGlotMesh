function durationLabel(value, count) {
  return count > 0 ? `${value.toFixed(2)} ms` : "No samples";
}

export default function MetricCards({ metrics }) {
  const cards = [
    {
      title: "Guest executions",
      value: metrics.guestCount,
      detail: "Accepted executions, including failures",
    },
    {
      title: "Guest mean",
      value: durationLabel(metrics.guestMeanMs, metrics.guestCount),
      detail: "Server runtime call; cold and warm samples mixed",
    },
    {
      title: "Guest failures",
      value: metrics.guestFailures,
      detail: "Script errors, timeouts, or runtime failures",
    },
    {
      title: "Mock delay mean",
      value: durationLabel(metrics.mockMeanMs, metrics.mockCount),
      detail: "Synthetic delay—not real REST service execution",
    },
  ];

  return (
    <div className="metric-grid">
      {cards.map((card) => (
        <article className="metric-card" key={card.title}>
          <h3>{card.title}</h3>
          <p className="metric-value">{card.value}</p>
          <p className="muted">{card.detail}</p>
        </article>
      ))}
    </div>
  );
}