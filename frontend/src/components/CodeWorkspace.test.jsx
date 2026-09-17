import { afterEach, describe, expect, it, vi } from "vitest";
import {
  cleanup,
  fireEvent,
  render,
  screen,
} from "@testing-library/react";

import CodeWorkspace from "./CodeWorkspace";

vi.mock("@monaco-editor/react", () => ({
  default: ({ value, onChange, options }) => (
    <textarea
      aria-label="Mock code editor"
      value={value}
      readOnly={options.readOnly}
      onChange={(event) => onChange(event.target.value)}
    />
  ),
}));

afterEach(cleanup);

function renderWorkspace(overrides = {}) {
  const props = {
    language: "python",
    code: "print('Hello')",
    onLanguageChange: vi.fn(),
    onCodeChange: vi.fn(),
    readOnly: false,
    ...overrides,
  };

  render(<CodeWorkspace {...props} />);

  return props;
}

describe("CodeWorkspace", () => {
  it("shows all three language buttons", () => {
    renderWorkspace();

    expect(screen.getByRole("button", { name: "Python" })).toBeTruthy();
    expect(screen.getByRole("button", { name: "JavaScript" })).toBeTruthy();
    expect(screen.getByRole("button", { name: "Java", exact: true })).toBeTruthy();
  });

  it("changes the selected language", () => {
    const props = renderWorkspace();

    fireEvent.click(
      screen.getByRole("button", { name: "JavaScript" }),
    );

    expect(props.onLanguageChange).toHaveBeenCalledWith("javascript");
  });

  it("forwards code edits", () => {
    const props = renderWorkspace();

    fireEvent.change(screen.getByLabelText("Mock code editor"), {
      target: { value: "print(42)" },
    });

    expect(props.onCodeChange).toHaveBeenCalledWith("print(42)");
  });

  it("explains that Java is reference-only", () => {
    renderWorkspace({
      language: "java",
      code: "class Example {}",
    });

    expect(screen.getByText(/Java is reference-only/)).toBeTruthy();
  });

  it("locks editing and language switching during execution", () => {
    renderWorkspace({ readOnly: true });

    expect(screen.getByLabelText("Mock code editor").readOnly).toBe(true);

    expect(
      screen.getByRole("button", { name: "JavaScript" }).disabled,
    ).toBe(true);
  });

  it("shows editor status information", () => {
    renderWorkspace({
      code: "line one\nline two",
    });

    expect(screen.getByText("Lines: 2")).toBeTruthy();
    expect(
      screen.getByText("Storage: session memory only"),
    ).toBeTruthy();
  });
});