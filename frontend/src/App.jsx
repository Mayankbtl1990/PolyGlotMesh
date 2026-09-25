import { useState } from "react";

import BackendStatus from "./components/BackendStatus";
import CodeWorkspace from "./components/CodeWorkspace";
import ExecutionConsole from "./components/ExecutionConsole";
import PricingAuditPanel from "./components/PricingAuditPanel";
import ResizableWorkspace from "./components/ResizableWorkspace";
import RuntimePolicyPanel from "./components/RuntimePolicyPanel";
import MetricsDashboard from "./components/MetricsDashboard";
import RuntimeDiagnosticsPanel from "./components/RuntimeDiagnosticsPanel";

import { useExecution } from "./hooks/useExecution";
import { useRunShortcut } from "./hooks/useRunShortcut"; 
import { INITIAL_SCRIPTS, LANGUAGES } from "./lib/languages";

export default function App() {
  const [language, setLanguage] = useState("python");
  const [scripts, setScripts] = useState(() => ({ ...INITIAL_SCRIPTS }));

  const execution = useExecution();

  const selectedLanguage = LANGUAGES.find(
    (item) => item.id === language,
  );

  const canRun =
    !execution.running &&
    selectedLanguage.executable &&
    Boolean(scripts[language].trim()) &&
    scripts[language].length <= 20_000;

  function updateCode(code) {
    setScripts((current) => ({
      ...current,
      [language]: code,
    }));
  }

  function changeLanguage(nextLanguage) {
    setLanguage(nextLanguage);
    execution.clear();
  }

  function runCurrentScript() {
    execution.run(language, scripts[language]);
  }

  useRunShortcut(runCurrentScript, canRun);

  return (
    <main className="app-shell">
      <header className="app-header">
        <div>
          <h1>PolyglotMesh</h1>
          <p>One workspace. Multiple languages.</p>
        </div>

        <BackendStatus />
      </header>

      <div className="toolbar">
        <p className="muted">
          Local development only. Deadlines are best-effort.
          Hard memory isolation is not enabled.
        </p>

        <div className="tabs">
          <button
            type="button"
            className="secondary-button"
            disabled={execution.running}
            onClick={execution.clear}
          >
            Clear console
          </button>

          <button
            type="button"
            className="primary-button"
            disabled={!canRun}
            onClick={runCurrentScript}
            title="Ctrl+Enter or Command+Enter"
          >
            {execution.running
              ? "Running…"
              : `Run ${selectedLanguage.label}`}
          </button>
        </div>
      </div>

      <ResizableWorkspace>
        <CodeWorkspace
          language={language}
          code={scripts[language]}
          onLanguageChange={changeLanguage}
          onCodeChange={updateCode}
          readOnly={execution.running}
        />

        <ExecutionConsole execution={execution} />
      </ResizableWorkspace>
            
      <MetricsDashboard />
      <PricingAuditPanel />
      <RuntimePolicyPanel />
      <RuntimeDiagnosticsPanel />
    </main>
  );
}