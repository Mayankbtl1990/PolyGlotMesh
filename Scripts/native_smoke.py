#!/usr/bin/env python3

import json
import sys
import urllib.error
import urllib.request


BASE_URL = (
    sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:8080"
).rstrip("/")


def request(path, payload=None):
    data = None if payload is None else json.dumps(payload).encode("utf-8")

    headers = {"Accept": "application/json"}

    if data is not None:
        headers["Content-Type"] = "application/json"

    http_request = urllib.request.Request(
        BASE_URL + path,
        data=data,
        headers=headers,
        method="GET" if payload is None else "POST",
    )

    with urllib.request.urlopen(http_request, timeout=60) as response:
        return json.load(response)


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


def main():
    health = request("/api/health")
    require(health["status"] == "UP", "Health check failed")

    diagnostics = request("/api/runtime/diagnostics")
    require(
        diagnostics["executionMode"] == "NATIVE_IMAGE",
        "Server is not running as a native executable",
    )

    python_result = request(
        "/api/executions",
        {
            "language": "python",
            "code": "print('NATIVE_PYTHON_OK')",
        },
    )
    require(
        "NATIVE_PYTHON_OK" in python_result["stdout"],
        "Native Python execution failed",
    )

    js_result = request(
        "/api/executions",
        {
            "language": "javascript",
            "code": "console.log('NATIVE_JS_OK');",
        },
    )
    require(
        "NATIVE_JS_OK" in js_result["stdout"],
        "Native JavaScript execution failed",
    )

    audit = request("/api/audits/pricing", {})
    require(audit["backingMapUpdated"], "Pricing map was not updated")
    require(audit["finalPrice"] == 85.0, "Unexpected final price")

    print("PASS: native health, execution mode, Python, JS, and pricing audit")
    print("NOTE: this does not validate native cancellation or full isolation")


if __name__ == "__main__":
    try:
        main()
    except urllib.error.HTTPError as failure:
        body = failure.read().decode("utf-8", errors="replace")
        print(f"FAIL: HTTP {failure.code}: {body}", file=sys.stderr)
        sys.exit(1)
    except Exception as failure:
        print(f"FAIL: {failure}", file=sys.stderr)
        sys.exit(1)