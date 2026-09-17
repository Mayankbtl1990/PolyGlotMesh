import { afterEach, describe, expect, it, vi } from "vitest";
import { executeCode, getHealth } from "./api";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("API client", () => {
  it("requests application health", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ status: "UP" }),
    });

    vi.stubGlobal("fetch", fetchMock);

    await expect(getHealth()).resolves.toEqual({ status: "UP" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/health",
      expect.objectContaining({
        headers: expect.objectContaining({
          Accept: "application/json",
        }),
      }),
    );
  });

  it("posts a script as JSON", async () => {
    const result = {
      language: "python",
      stdout: "Hello\n",
      stderr: "",
      durationMs: 1,
      outputTruncated: false,
    };

    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => result,
    });

    vi.stubGlobal("fetch", fetchMock);

    await expect(
      executeCode({
        language: "python",
        code: "print('Hello')",
      }),
    ).resolves.toEqual(result);

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/executions",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({
          language: "python",
          code: "print('Hello')",
        }),
      }),
    );
  });

  it("preserves backend script error messages", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn().mockResolvedValue({
      ok: false,
      status: 422,
      json: async () => ({
        code: "SCRIPT_ERROR",
        message: "ZeroDivisionError: division by zero",
      }),
    }),
  );

  await expect(
    executeCode({
      language: "python",
      code: "print(1 / 0)",
    }),
  ).rejects.toMatchObject({
    status: 422,
    code: "SCRIPT_ERROR",
    message: "ZeroDivisionError: division by zero",
  });
});

it("explains network connection failures", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn().mockRejectedValue(new TypeError("Failed to fetch")),
  );

  await expect(getHealth()).rejects.toMatchObject({
    code: "NETWORK_ERROR",
    status: 0,
  });
});

it("handles non-JSON server responses", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn().mockResolvedValue({
      ok: false,
      status: 502,
      json: async () => {
        throw new SyntaxError("Unexpected token");
      },
    }),
  );

  await expect(getHealth()).rejects.toMatchObject({
    code: "INVALID_RESPONSE",
    status: 502,
  });
});
});