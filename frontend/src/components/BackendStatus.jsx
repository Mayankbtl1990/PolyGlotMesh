import { useEffect, useState } from "react";
import { getHealth } from "../lib/api";

export default function BackendStatus() {
  const [status, setStatus] = useState("checking");

  useEffect(() => {
    let active = true;

    async function check() {
      try {
        const response = await getHealth();

        if (active) {
          setStatus(response.status === "UP" ? "online" : "offline");
        }
      } catch {
        if (active) {
          setStatus("offline");
        }
      }
    }

    check();

    const interval = window.setInterval(check, 15_000);

    return () => {
      active = false;
      window.clearInterval(interval);
    };
  }, []);

  return (
    <span className={`backend-status ${status}`} role="status">
      Backend: {status}
    </span>
  );
}