import { useEffect } from "react";

export function useRunShortcut(callback, enabled) {
  useEffect(() => {
    function handleKeyDown(event) {
      const modifierPressed = event.ctrlKey || event.metaKey;

      if (!modifierPressed || event.key !== "Enter" || event.repeat) {
        return;
      }

      event.preventDefault();

      if (enabled) {
        callback();
      }
    }

    window.addEventListener("keydown", handleKeyDown, true);

    return () => {
      window.removeEventListener("keydown", handleKeyDown, true);
    };
  }, [callback, enabled]);
}
