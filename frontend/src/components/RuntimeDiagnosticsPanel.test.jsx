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
  render,
  screen,
} from "@testing-library/react";

import { getRuntimeDiagnostics } from "../lib/api";
import RuntimeDiagnosticsPanel from "./RuntimeDiagnosticsPanel";

vi.mock("../lib/api", () => ({
  getRuntimeDiagnostics: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();
});

afterEach(cleanup);

describe("RuntimeDiagnosticsPanel", () => {
  it("displays the actual JVM execution mode", async () => {
    getRuntimeDiagnostics.mockResolvedValue({
      executionMode: "JVM",
      javaVersion: "21",
      engineVersion: "24.1.2",
      operatingSystem: "Linux",
      architecture: "amd64",
      installedLanguages: [
        { id: "python", name: "Python", version: "24.1.2" },
        { id: "js", name: "JavaScript", version: "24.1.2" },
      ],
    });

    render(<RuntimeDiagnosticsPanel />);

    expect(await screen.findByText("JVM")).toBeTruthy();
    expect(screen.getByText(/Python \(python\)/)).toBeTruthy();
    expect(screen.queryByText("NATIVE_IMAGE")).toBeNull();
  });

  it("shows diagnostic request failures", async () => {
    getRuntimeDiagnostics.mockRejectedValue(
      new Error("Diagnostics unavailable"),
    );

    render(<RuntimeDiagnosticsPanel />);

    expect((await screen.findByRole("alert")).textContent)
      .toBe("Diagnostics unavailable");
  });
});