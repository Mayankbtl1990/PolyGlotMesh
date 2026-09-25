import { afterEach, describe, expect, it, vi } from "vitest";

import {
  getExecutionMetrics,
  getRuntimeDiagnostics,
  runMockRestBaseline,
} from "./api";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("observability API client", () => {
  it.each([
    [getExecutionMetrics, "/api/metrics"],
    [getRuntimeDiagnostics, "/api/runtime/diagnostics"],
  ])("requests an observability endpoint", async (operation, path) => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ available: true }),
    });

    vi.stubGlobal("fetch", fetchMock);

    await operation();

    expect(fetchMock).toHaveBeenCalledWith(
      path,
      expect.any(Object),
    );
  });

  it("posts a mock baseline request", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ observedMs: 52 }),
    });

    vi.stubGlobal("fetch", fetchMock);

    await runMockRestBaseline();

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/benchmarks/mock-rest",
      expect.objectContaining({
        method: "POST",
        body: "{}",
      }),
    );
  });
});