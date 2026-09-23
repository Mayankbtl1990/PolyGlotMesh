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
} from "@testing-library/react";

import { runPricingAudit } from "../lib/api";
import PricingAuditPanel from "./PricingAuditPanel";

vi.mock("../lib/api", () => ({
  runPricingAudit: vi.fn(),
}));

beforeEach(() => {
  vi.resetAllMocks();
});

afterEach(cleanup);

describe("PricingAuditPanel", () => {
  it("shows successful map update results", async () => {
    runPricingAudit.mockResolvedValue({
      basePrice: 100,
      discount: 0.15,
      finalPrice: 85,
      backingMapUpdated: true,
      execution: {
        stdout: "Python updated finalPrice: 85.0\n",
      },
    });

    render(<PricingAuditPanel />);

    fireEvent.click(
      screen.getByRole("button", { name: "Run pricing audit" }),
    );

    expect(await screen.findByText("85.00")).toBeTruthy();
    expect(screen.getByText("Yes")).toBeTruthy();
    expect(screen.getByText("15%")).toBeTruthy();
  });

  it("shows audit request failures", async () => {
    runPricingAudit.mockRejectedValue(
      new Error("Audit runtime unavailable"),
    );

    render(<PricingAuditPanel />);

    fireEvent.click(
      screen.getByRole("button", { name: "Run pricing audit" }),
    );

    expect((await screen.findByRole("alert")).textContent)
      .toBe("Audit runtime unavailable");
  });
});