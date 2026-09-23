import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { act, cleanup, renderHook } from "@testing-library/react";

import { executeCode } from "../lib/api";
import { useExecution } from "./useExecution";

vi.mock("../lib/api", () => ({
  executeCode: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();
});

afterEach(cleanup);

describe("useExecution", () => {
  it("stores a successful execution response", async () => {
    executeCode.mockResolvedValue({
      language: "python",
      stdout: "42\n",
      stderr: "",
      durationMs: 5,
      outputTruncated: false,
    });

    const { result } = renderHook(() => useExecution());

    await act(async () => {
      await result.current.run("python", "print(42)");
    });

    expect(result.current.result.stdout).toBe("42\n");
    expect(result.current.running).toBe(false);
    expect(result.current.error).toBe("");
  });

  it("shows a failed execution message", async () => {
    executeCode.mockRejectedValue(new Error("Script failed"));

    const { result } = renderHook(() => useExecution());

    await act(async () => {
      await result.current.run("python", "print(1 / 0)");
    });

    expect(result.current.error).toBe("Script failed");
    expect(result.current.running).toBe(false);
  });

  it("rejects Java without calling the API", async () => {
    const { result } = renderHook(() => useExecution());

    await act(async () => {
      await result.current.run("java", "class Example {}");
    });

    expect(executeCode).not.toHaveBeenCalled();
    expect(result.current.error).toContain("not supported");
  });

  it("prevents duplicate in-flight execution requests", async () => {
    let finishRequest;

    executeCode.mockImplementation(
      () =>
        new Promise((resolve) => {
          finishRequest = resolve;
        }),
    );

    const { result } = renderHook(() => useExecution());

    let firstRun;

    act(() => {
      firstRun = result.current.run("python", "print(1)");
      result.current.run("python", "print(2)");
    });

    expect(executeCode).toHaveBeenCalledTimes(1);
    expect(result.current.running).toBe(true);

    await act(async () => {
      finishRequest({
        language: "python",
        stdout: "1\n",
        stderr: "",
        durationMs: 1,
        outputTruncated: false,
      });

      await firstRun;
    });

    expect(result.current.running).toBe(false);
  });

  it("preserves timeout output and error code", async () => {
    const failure = Object.assign(new Error("Evaluation deadline reached"), {
        code: "EXECUTION_TIMEOUT",
        execution: {
        language: "python",
        stdout: "started\n",
        stderr: "",
        durationMs: 5010,
        outputTruncated: false,
        },
        guestStack: [],
    });

    executeCode.mockRejectedValue(failure);

    const { result } = renderHook(() => useExecution());

    await act(async () => {
        await result.current.run("python", "print('started')");
    });

    expect(result.current.errorCode).toBe("EXECUTION_TIMEOUT");
    expect(result.current.result.stdout).toBe("started\n");
    expect(result.current.running).toBe(false);
    });

    it("clears error details with the console", async () => {
    executeCode.mockRejectedValue(
    Object.assign(new Error("Expected failure"), {
        code: "SCRIPT_ERROR",
        guestStack: ["main (script.js:1)"],
    }),
    );

    const { result } = renderHook(() => useExecution());

    await act(async () => {
    await result.current.run("javascript", "throw new Error('fail')");
    });

    act(() => {
    result.current.clear();
    });

    expect(result.current.error).toBe("");
    expect(result.current.errorCode).toBe("");
    expect(result.current.guestStack).toEqual([]);
    expect(result.current.result).toBeNull();
    });
});