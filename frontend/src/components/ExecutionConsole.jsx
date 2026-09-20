import ConsolePanel from "./ConsolePanel";

export default function ExecutionConsole({ execution }) {
  const { result, error, errorCode, guestStack, running } = execution;

  return (
    <ConsolePanel
      stdout={result?.stdout ?? ""}
      stderr={result?.stderr ?? ""}
      durationMs={result?.durationMs ?? null}
      outputTruncated={result?.outputTruncated ?? false}
      error={error}
      errorCode={errorCode}
      guestStack={guestStack}
      running={running}
    />
  );
}