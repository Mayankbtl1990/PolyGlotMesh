import { afterEach, describe, expect, it, vi } from "vitest";

import {
  createSavedScript,
  getSavedScript,
  listSavedScripts,
  updateSavedScript,
} from "./api";

afterEach(() => {
  vi.unstubAllGlobals();
});

function mockFetch(body = {}) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: true,
    status: 200,
    json: async () => body,
  });

  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("script library API", () => {
  it("lists scripts", async () => {
    const fetchMock = mockFetch([]);

    await listSavedScripts();

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/scripts",
      expect.any(Object),
    );
  });

  it("loads a script by encoded identifier", async () => {
    const fetchMock = mockFetch();

    await getSavedScript("example/id");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/scripts/example%2Fid",
      expect.any(Object),
    );
  });

  it("creates a script", async () => {
    const fetchMock = mockFetch();

    const script = {
      name: "Pricing",
      language: "python",
      code: "print(85)",
    };

    await createSavedScript(script);

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/scripts",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify(script),
      }),
    );
  });

  it("updates a saved script", async () => {
    const fetchMock = mockFetch();

    const script = {
      name: "Pricing",
      language: "python",
      code: "print(90)",
    };

    await updateSavedScript("script-id", script);

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/scripts/script-id",
      expect.objectContaining({
        method: "PUT",
        body: JSON.stringify(script),
      }),
    );
  });
});