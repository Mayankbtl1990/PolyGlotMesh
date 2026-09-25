// @vitest-environment jsdom

import { afterEach, describe, expect, it, vi } from "vitest";
import {
  cleanup,
  fireEvent,
  renderHook,
} from "@testing-library/react";

import { useRunShortcut } from "./useRunShortcut";

afterEach(cleanup);

describe("useRunShortcut", () => {
  it("runs on Ctrl+Enter when enabled", () => {
    const run = vi.fn();

    renderHook(() => useRunShortcut(run, true));

    fireEvent.keyDown(window, {
      key: "Enter",
      ctrlKey: true,
    });

    expect(run).toHaveBeenCalledTimes(1);
  });

  it("does not run when disabled", () => {
    const run = vi.fn();

    renderHook(() => useRunShortcut(run, false));

    fireEvent.keyDown(window, {
      key: "Enter",
      metaKey: true,
    });

    expect(run).not.toHaveBeenCalled();
  });

  it("removes the listener when unmounted", () => {
    const run = vi.fn();

    const { unmount } = renderHook(() =>
      useRunShortcut(run, true),
    );

    unmount();

    fireEvent.keyDown(window, {
      key: "Enter",
      ctrlKey: true,
    });

    expect(run).not.toHaveBeenCalled();
  });
});