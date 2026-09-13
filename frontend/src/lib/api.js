export class ApiClientError extends Error {
  constructor(message, status, code) {
    super(message);
    this.name = "ApiClientError";
    this.status = status;
    this.code = code;
  }
}

async function request(path, options = {}) {
  let response;

  try {
    response = await fetch(path, {
      ...options,
      headers: {
        Accept: "application/json",
        ...options.headers,
      },
    });
  } catch {
    throw new ApiClientError(
      "Cannot reach the backend. Confirm Spring Boot is running.",
      0,
      "NETWORK_ERROR",
    );
  }

  let body;

  try {
    body = await response.json();
  } catch {
    throw new ApiClientError(
      "The server returned an unreadable response.",
      response.status,
      "INVALID_RESPONSE",
    );
  }

  if (!response.ok) {
    throw new ApiClientError(
      body.message || `Request failed with status ${response.status}.`,
      response.status,
      body.code || "REQUEST_FAILED",
    );
  }

  return body;
}

export function getHealth() {
  return request("/api/health");
}

export function executeCode({ language, code }) {
  return request("/api/executions", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ language, code }),
  });
}