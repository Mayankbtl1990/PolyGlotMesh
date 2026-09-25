import { useEffect, useState } from "react";
import { getExecutionMetrics } from "../lib/api";

export function useMetrics() {
  const [metrics, setMetrics] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    let timer;

    async function poll() {
      try {
        const response = await getExecutionMetrics();

        if (active) {
          setMetrics(response);
          setError("");
        }
      } catch (failure) {
        if (active) {
          setError(failure.message || "Cannot refresh metrics.");
        }
      } finally {
        if (active) {
          timer = window.setTimeout(poll, 3000);
        }
      }
    }

    poll();

    return () => {
      active = false;
      window.clearTimeout(timer);
    };
  }, []);

  return { metrics, error };
}