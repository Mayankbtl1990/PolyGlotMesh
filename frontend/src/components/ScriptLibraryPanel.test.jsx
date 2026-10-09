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

import {
  createSavedScript,
  getSavedScript,
  listSavedScripts,
} from "../lib/api";

import ScriptLibraryPanel from "./ScriptLibraryPanel";

vi.mock("../lib/api", () => ({
  createSavedScript: vi.fn(),
  getSavedScript: vi.fn(),
  listSavedScripts: vi.fn(),
  updateSavedScript: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();
  listSavedScripts.mockResolvedValue([]);
});

afterEach(cleanup);

describe("ScriptLibraryPanel", () => {
  it("saves the current editor content", async () => {
    createSavedScript.mockResolvedValue({
      id: "one",
      name: "Pricing",
      language: "python",
      code: "print(85)",
      updatedAt: "2026-01-01T00:00:00Z",
    });

    render(
      <ScriptLibraryPanel
        language="python"
        code="print(85)"
        onLoad={vi.fn()}
        onBusyChange={vi.fn()}
      />,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Script name").disabled).toBe(false);
    });

    fireEvent.change(screen.getByLabelText("Script name"), {
      target: { value: "Pricing" },
    });

    fireEvent.click(
      screen.getByRole("button", { name: "Save as new" }),
    );

    expect(
      await screen.findByText('Saved "Pricing" as a new script.'),
    ).toBeTruthy();

    expect(createSavedScript).toHaveBeenCalledWith({
      name: "Pricing",
      language: "python",
      code: "print(85)",
    });
  });

  it("loads a selected script into the editor callback", async () => {
    const script = {
      id: "one",
      name: "Pricing",
      language: "python",
      code: "print(85)",
      updatedAt: "2026-01-01T00:00:00Z",
    };

    listSavedScripts.mockResolvedValue([script]);
    getSavedScript.mockResolvedValue(script);

    const onLoad = vi.fn().mockReturnValue(true);

    render(
      <ScriptLibraryPanel
        language="python"
        code="print(1)"
        onLoad={onLoad}
        onBusyChange={vi.fn()}
      />,
    );

    await screen.findByRole("option", { name: "Pricing — python" });

    await waitFor(() => {
      expect(screen.getByLabelText("Saved scripts").disabled).toBe(false);
    });

    fireEvent.change(screen.getByLabelText("Saved scripts"), {
      target: { value: "one" },
    });

    fireEvent.click(
      screen.getByRole("button", { name: "Load selected script" }),
    );

    await waitFor(() => {
      expect(onLoad).toHaveBeenCalledWith(script);
    });
  });
});