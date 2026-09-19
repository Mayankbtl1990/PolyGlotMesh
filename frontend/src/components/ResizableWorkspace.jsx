import { useRef, useState } from "react";

function clamp(value) {
  return Math.min(75, Math.max(30, value));
}

export default function ResizableWorkspace({ children }) {
  const containerRef = useRef(null);
  const dragging = useRef(false);
  const [leftPercent, setLeftPercent] = useState(60);

  function updateFromPointer(event) {
    const bounds = containerRef.current?.getBoundingClientRect();

    if (!bounds || bounds.width === 0) {
      return;
    }

    const next = ((event.clientX - bounds.left) / bounds.width) * 100;
    setLeftPercent(clamp(next));
  }

  function startDragging(event) {
    dragging.current = true;
    event.currentTarget.setPointerCapture(event.pointerId);
    updateFromPointer(event);
  }

  function stopDragging() {
    dragging.current = false;
  }

  function handleKeyDown(event) {
    if (event.key === "ArrowLeft") {
      event.preventDefault();
      setLeftPercent((current) => clamp(current - 2));
    }

    if (event.key === "ArrowRight") {
      event.preventDefault();
      setLeftPercent((current) => clamp(current + 2));
    }

    if (event.key === "Home") {
      event.preventDefault();
      setLeftPercent(30);
    }

    if (event.key === "End") {
      event.preventDefault();
      setLeftPercent(75);
    }
  }

  const [editor, consolePanel] = children;

  return (
    <div
      ref={containerRef}
      className="resizable-workspace"
      style={{
        gridTemplateColumns: `${leftPercent}fr 10px ${100 - leftPercent}fr`,
      }}
    >
      {editor}

      <div
        className="resize-handle"
        role="separator"
        aria-label="Resize editor and console"
        aria-orientation="vertical"
        aria-valuemin={30}
        aria-valuemax={75}
        aria-valuenow={Math.round(leftPercent)}
        tabIndex={0}
        onKeyDown={handleKeyDown}
        onPointerDown={startDragging}
        onPointerMove={(event) => {
          if (dragging.current) {
            updateFromPointer(event);
          }
        }}
        onPointerUp={stopDragging}
        onPointerCancel={stopDragging}
        onLostPointerCapture={stopDragging}
      />

      {consolePanel}
    </div>
  );
}