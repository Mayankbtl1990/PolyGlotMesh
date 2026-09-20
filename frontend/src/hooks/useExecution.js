import { useRef, useState } from "react";
import { executeCode } from "../lib/api";

export function useExecution() {
  const requestInFlight = useRef(false);

  const [running, setRunning] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [errorCode, setErrorCode] = useState("");
  const [guestStack, setGuestStack] = useState([]);

  function resetOutput() {
    setResult(null);
    setError("");
    setErrorCode("");
    setGuestStack([]);
  }

  async function run(language, code) {
    if (requestInFlight.current) {
      return;
    }

    resetOutput();

    if (!["python", "javascript"].includes(language)) {
      setError("Java execution is not supported.");
      setErrorCode("UNSUPPORTED_LANGUAGE");
      return;
    }

    if (!code.trim()) {
      setError("Enter some code before running.");
      setErrorCode("EMPTY_CODE");
      return;
    }

    if (code.length > 20_000) {
      setError("Code must not exceed 20,000 characters.");
      setErrorCode("CODE_TOO_LARGE");
      return;
    }

    requestInFlight.current = true;
    setRunning(true);

    try {
      const response = await executeCode({ language, code });
      setResult(response);
    } catch (failure) {
      setError(failure.message || "Execution failed.");
      setErrorCode(failure.code || "REQUEST_FAILED");
      setResult(failure.execution ?? null);
      setGuestStack(failure.guestStack ?? []);
    } finally {
      requestInFlight.current = false;
      setRunning(false);
    }
  }

  function clear() {
    if (!requestInFlight.current) {
      resetOutput();
    }
  }

  return {
    run,
    clear,
    running,
    result,
    error,
    errorCode,
    guestStack,
  };
}