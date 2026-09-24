import { afterEach, describe, expect, it } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";

import MetricCards from "./MetricCards";

afterEach(cleanup);

describe("MetricCards", () => {
  it("formats measured durations", () => {
    render(
      <MetricCards
        metrics={{
          guestCount: 10,
          guestFailures: 2,
          guestMeanMs: 12.345,
          mockCount: 1,
          mockMeanMs: 52.789,
        }}
      />,
    );

    expect(screen.getByText("12.35 ms")).toBeTruthy();
    expect(screen.getByText("52.79 ms")).toBeTruthy();
  });

  it("does not present missing samples as zero latency", () => {
    render(
      <MetricCards
        metrics={{
          guestCount: 0,
          guestFailures: 0,
          guestMeanMs: 0,
          mockCount: 0,
          mockMeanMs: 0,
        }}
      />,
    );

    expect(screen.getAllByText("No samples")).toHaveLength(2);
  });
});