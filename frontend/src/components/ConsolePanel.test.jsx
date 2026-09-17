import { afterEach, describe, expect, it } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";

import ConsolePanel from "./ConsolePanel";

afterEach(cleanup);

describe("ConsolePanel", () => {
  it("renders stdout and execution time", () => {
    render(
      <ConsolePanel
        stdout="Hello from Python"
        durationMs={12.345}
      />,
    );

    expect(screen.getByText("Hello from Python")).toBeTruthy();
    expect(
      screen.getByText("Server execution: 12.35 ms"),
    ).toBeTruthy();
  });

  it("renders an execution error as an alert", () => {
    render(<ConsolePanel error="Script failed" />);

    expect(screen.getByRole("alert").textContent).toBe("Script failed");
  });

  it("shows output truncation", () => {
    render(<ConsolePanel stdout="abc" outputTruncated />);

    expect(
      screen.getByText(/Output was truncated/),
    ).toBeTruthy();
  });

  it("renders script output as text rather than HTML", () => {
    const { container } = render(
      <ConsolePanel stdout="<img src=x onerror=alert(1)>" />,
    );

    expect(container.querySelector("img")).toBeNull();
    expect(
      screen.getByText("<img src=x onerror=alert(1)>"),
    ).toBeTruthy();
  });
});