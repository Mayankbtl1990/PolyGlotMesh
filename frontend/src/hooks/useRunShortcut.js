import { useEffect } from "react";

export function useRunShortcut(onRun, canRun = true) {
  useEffect(() => {
    function handleKeyDown(event) {
      if ((event.ctrlKey || event.metaKey) && event.key === "Enter") {
        if (canRun) {
          event.preventDefault();
          onRun();
        }
      }
    }

    window.addEventListener("keydown", handleKeyDown);
    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [onRun, canRun]);
}

export default useRunShortcut;