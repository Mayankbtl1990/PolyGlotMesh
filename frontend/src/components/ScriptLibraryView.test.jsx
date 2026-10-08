import { afterEach, describe, expect, it, vi } from "vitest";

import {
  cleanup,
  fireEvent,
  render,
  screen,
} from "@testing-library/react";

import ScriptLibraryView from "./ScriptLibraryView";

afterEach(cleanup);

function renderLibrary(overrides = {}) {
  const props = {
    scripts: [
      {
        id: "one",
        name: "Pricing",
        language: "python",
      },
    ],
    name: "New pricing",
    onNameChange: vi.fn(),
    selectedId: "one",
    onSelect: vi.fn(),
    onSaveNew: vi.fn(),
    onUpdate: vi.fn(),
    onLoad: vi.fn(),
    busy: false,
    canSave: true,
    canUpdate: true,
    message: "",
    error: "",
    ...overrides,
  };

  render(<ScriptLibraryView {...props} />);
  return props;
}

describe("ScriptLibraryView", () => {
  it("forwards save and load actions", () => {
    const props = renderLibrary();

    fireEvent.click(
      screen.getByRole("button", { name: "Save as new" }),
    );

    fireEvent.click(
      screen.getByRole("button", { name: "Load selected script" }),
    );

    expect(props.onSaveNew).toHaveBeenCalledTimes(1);
    expect(props.onLoad).toHaveBeenCalledTimes(1);
  });

  it("disables controls while busy", () => {
    renderLibrary({ busy: true });

    expect(screen.getByLabelText("Script name").disabled).toBe(true);

    expect(
      screen.getByRole("button", { name: "Save as new" }).disabled,
    ).toBe(true);
  });

  it("does not allow updates without a loaded target", () => {
    renderLibrary({ canUpdate: false });

    expect(
      screen.getByRole("button", { name: "Update loaded script" }).disabled,
    ).toBe(true);
  });

  it("shows operation errors", () => {
    renderLibrary({ error: "Storage unavailable" });

    expect(screen.getByRole("alert").textContent)
      .toBe("Storage unavailable");
  });
});