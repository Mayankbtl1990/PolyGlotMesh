import { useRef, useState } from "react";
import { executeCode } from "../lib/api";

export function useExecution() {
  const requestInFlight = useRef(false);

  const [running, setRunning] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  async function run(language, code) {
    if (requestInFlight.current) {
      return;
    }

    if (!["python", "javascript"].includes(language)) {
      setError("Java execution is not supported in Week 1.");
      return;
    }

    if (!code.trim()) {
      setError("Enter some code before running.");
      return;
    }

    if (code.length > 20_000) {
      setError("Code must not exceed 20,000 characters.");
      return;
    }

    requestInFlight.current = true;
    setRunning(true);
    setError("");
    setResult(null);

    try {
      const response = await executeCode({ language, code });
      setResult(response);
    } catch (failure) {
      setError(failure.message || "Execution failed.");
    } finally {
      requestInFlight.current = false;
      setRunning(false);
    }
  }

  function clear() {
    if (requestInFlight.current) {
      return;
    }

    setResult(null);
    setError("");
  }

  return {
    run,
    clear,
    running,
    result,
    error,
  };
}