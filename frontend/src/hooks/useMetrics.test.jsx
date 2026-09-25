import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import {
  act,
  cleanup,
  renderHook,
} from "@testing-library/react";

import { getExecutionMetrics } from "../lib/api";
import { useMetrics } from "./useMetrics";

vi.mock("../lib/api", () => ({
  getExecutionMetrics: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();
  vi.useFakeTimers();
});

afterEach(() => {
  cleanup();
  vi.useRealTimers();
});

describe("useMetrics", () => {
  it("loads metrics and refreshes after three seconds", async () => {
    getExecutionMetrics.mockResolvedValue({
      guestCount: 2,
      recent: [],
    });

    const { result } = renderHook(() => useMetrics());

    await act(async () => {
      await Promise.resolve();
    });

    expect(result.current.metrics.guestCount).toBe(2);

    await act(async () => {
      await vi.advanceTimersByTimeAsync(3000);
    });

    expect(getExecutionMetrics).toHaveBeenCalledTimes(2);
  });

  it("does not overlap pending polls", async () => {
    getExecutionMetrics.mockReturnValue(new Promise(() => {}));

    renderHook(() => useMetrics());

    await act(async () => {
      await vi.advanceTimersByTimeAsync(15_000);
    });

    expect(getExecutionMetrics).toHaveBeenCalledTimes(1);
  });

  it("stops future polling after unmount", async () => {
    getExecutionMetrics.mockResolvedValue({
      guestCount: 0,
      recent: [],
    });

    const { unmount } = renderHook(() => useMetrics());

    await act(async () => {
      await Promise.resolve();
    });

    unmount();

    await act(async () => {
      await vi.advanceTimersByTimeAsync(9000);
    });

    expect(getExecutionMetrics).toHaveBeenCalledTimes(1);
  });

  it("retains the last sample when refresh fails", async () => {
    getExecutionMetrics
      .mockResolvedValueOnce({
        guestCount: 4,
        recent: [],
      })
      .mockRejectedValueOnce(new Error("Backend offline"));

    const { result } = renderHook(() => useMetrics());

    await act(async () => {
      await Promise.resolve();
    });

    await act(async () => {
      await vi.advanceTimersByTimeAsync(3000);
    });

    expect(result.current.metrics.guestCount).toBe(4);
    expect(result.current.error).toBe("Backend offline");
  });
});