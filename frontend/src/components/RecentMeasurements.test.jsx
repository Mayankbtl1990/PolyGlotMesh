import { afterEach, describe, expect, it } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";

import RecentMeasurements from "./RecentMeasurements";

afterEach(cleanup);

describe("RecentMeasurements", () => {
  it("shows an empty history message", () => {
    render(<RecentMeasurements samples={[]} />);

    expect(screen.getByText("No recent measurements.")).toBeTruthy();
  });

  it("distinguishes failed guest samples from mock delays", () => {
    render(
      <RecentMeasurements
        samples={[
          {
            category: "GUEST",
            language: "python",
            success: false,
            durationMs: 12,
            recordedAt: "2026-01-01T00:00:00Z",
          },
          {
            category: "MOCK_DELAY",
            language: "simulated-rest",
            success: true,
            durationMs: 51.234,
            recordedAt: "2026-01-01T00:00:01Z",
          },
        ]}
      />,
    );

    expect(screen.getByText("python")).toBeTruthy();
    expect(screen.getByText("Failed")).toBeTruthy();
    expect(screen.getByText("Mock delay")).toBeTruthy();
    expect(screen.getByText("51.23 ms")).toBeTruthy();
  });

  it("shows at most ten recent samples", () => {
    const samples = Array.from({ length: 20 }, (_, index) => ({
      category: "GUEST",
      language: "python",
      success: true,
      durationMs: index,
      recordedAt: String(index),
    }));

    render(<RecentMeasurements samples={samples} />);

    // One header row plus ten sample rows.
    expect(screen.getAllByRole("row")).toHaveLength(11);
  });
});