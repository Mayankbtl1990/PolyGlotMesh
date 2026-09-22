import { afterEach, describe, expect, it } from "vitest";
import {
  cleanup,
  fireEvent,
  render,
  screen,
} from "@testing-library/react";

import ResizableWorkspace from "./ResizableWorkspace";

afterEach(cleanup);

describe("ResizableWorkspace", () => {
  it("renders editor and console children", () => {
    render(
      <ResizableWorkspace>
        <div>Editor content</div>
        <div>Console content</div>
      </ResizableWorkspace>,
    );

    expect(screen.getByText("Editor content")).toBeTruthy();
    expect(screen.getByText("Console content")).toBeTruthy();
  });

  it("supports keyboard resizing with bounds", () => {
    render(
      <ResizableWorkspace>
        <div>Editor</div>
        <div>Console</div>
      </ResizableWorkspace>,
    );

    const separator = screen.getByRole("separator");

    expect(separator.getAttribute("aria-valuenow")).toBe("60");

    fireEvent.keyDown(separator, { key: "ArrowRight" });
    expect(separator.getAttribute("aria-valuenow")).toBe("62");

    fireEvent.keyDown(separator, { key: "Home" });
    expect(separator.getAttribute("aria-valuenow")).toBe("30");

    fireEvent.keyDown(separator, { key: "ArrowLeft" });
    expect(separator.getAttribute("aria-valuenow")).toBe("30");

    fireEvent.keyDown(separator, { key: "End" });
    expect(separator.getAttribute("aria-valuenow")).toBe("75");
  });
});