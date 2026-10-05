import { useState } from "react";

import BackendStatus from "./components/BackendStatus";
import CodeWorkspace from "./components/CodeWorkspace";
import ExecutionConsole from "./components/ExecutionConsole";
import PricingAuditPanel from "./components/PricingAuditPanel";
import ResizableWorkspace from "./components/ResizableWorkspace";
import RuntimePolicyPanel from "./components/RuntimePolicyPanel";
import MetricsDashboard from "./components/MetricsDashboard";
import RuntimeDiagnosticsPanel from "./components/RuntimeDiagnosticsPanel";
import ScriptLibraryPanel from "./components/ScriptLibraryPanel";

import { useExecution } from "./hooks/useExecution";
import { useRunShortcut } from "./hooks/useRunShortcut"; 
import { INITIAL_SCRIPTS, LANGUAGES } from "./lib/languages";

export default function App() {
  const [language, setLanguage] = useState("python");
  const [scripts, setScripts] = useState(() => ({ ...INITIAL_SCRIPTS }));
  const [libraryBusy, setLibraryBusy] = useState(false);

  const execution = useExecution();

  const editorLocked = execution.running || libraryBusy || dataBusy;
  const selectedLanguage = LANGUAGES.find(
    (item) => item.id === language,
  );

  const canRun =
    !editorLocked &&
    !libraryBusy &&
    selectedLanguage.executable &&
    Boolean(scripts[language].trim()) &&
    scripts[language].length <= 20_000;

  function updateCode(code) {
    setScripts((current) => ({
      ...current,
      [language]: code,
    }));
  }

  function insertBindingExample(example) {
    if (
      !window.confirm(
        "Replace the current tab with the data-binding example?",
      )
    ) {
      return;
    }

    updateCode(example);
    execution.clear();
  }

  function loadSavedScript(script) {
    const existingCode = scripts[script.language];

    if (
      existingCode !== script.code &&
      !window.confirm(
        `Replace the current ${script.language} tab? Unsaved changes in that tab will be lost.`,
      )
    ) {
      return false;
    }

    setScripts((current) => ({
      ...current,
      [script.language]: script.code,
    }));

    setLanguage(script.language);
    execution.clear();

    return true;
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
            disabled={editorLocked}
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
          readOnly={editorLocked}
        />

        <ExecutionConsole execution={execution} />
      </ResizableWorkspace>

      <ScriptLibraryPanel
        language={language}
        code={scripts[language]}
        disabled={execution.running || dataBusy}
        onLoad={loadSavedScript}
        onBusyChange={setLibraryBusy}
      />

      <DataBindingPanel
        language={language}
        code={scripts[language]}
        disabled={execution.running || libraryBusy}
        onBusyChange={setDataBusy}
        onUseExample={insertBindingExample}
      />
      <MetricsDashboard /> 
      <PricingAuditPanel />
      <RuntimePolicyPanel />
      <RuntimeDiagnosticsPanel />
    </main>
  );
}