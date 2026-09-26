import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import {
  cleanup,
  fireEvent,
  render,
  screen,
  waitFor,
} from "@testing-library/react";

import { useMetrics } from "../hooks/useMetrics";
import { runMockRestBaseline } from "../lib/api";
import MetricsDashboard from "./MetricsDashboard";

vi.mock("../hooks/useMetrics", () => ({
  useMetrics: vi.fn(),
}));

vi.mock("../lib/api", () => ({
  runMockRestBaseline: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();

  useMetrics.mockReturnValue({
    metrics: {
      guestCount: 0,
      guestFailures: 0,
      guestMeanMs: 0,
      mockCount: 0,
      mockMeanMs: 0,
      recent: [],
    },
    error: "",
  });
});

afterEach(cleanup);

describe("MetricsDashboard", () => {
  it("explains that the baseline is artificial", () => {
    render(<MetricsDashboard />);

    expect(
      screen.getByText(/does not execute an equivalent script/),
    ).toBeTruthy();
  });

  it("runs a requested mock baseline", async () => {
    runMockRestBaseline.mockResolvedValue({
      observedMs: 52,
    });

    render(<MetricsDashboard />);

    fireEvent.click(
      screen.getByRole("button", { name: "Sample mock REST delay" }),
    );

    await waitFor(() => {
      expect(runMockRestBaseline).toHaveBeenCalledTimes(1);
      expect(
        screen.getByRole("button", { name: "Sample mock REST delay" })
          .disabled,
      ).toBe(false);
    });
  });

  it("warns when displayed metrics may be stale", () => {
    useMetrics.mockReturnValue({
      metrics: null,
      error: "Backend unavailable",
    });

    render(<MetricsDashboard />);

    expect(screen.getByRole("alert").textContent)
      .toContain("may be stale");
  });

  it("shows mock request failures", async () => {
    runMockRestBaseline.mockRejectedValue(
      new Error("Mock endpoint unavailable"),
    );

    render(<MetricsDashboard />);

    fireEvent.click(
      screen.getByRole("button", { name: "Sample mock REST delay" }),
    );

    expect((await screen.findByRole("alert")).textContent)
      .toBe("Mock endpoint unavailable");
  });
});