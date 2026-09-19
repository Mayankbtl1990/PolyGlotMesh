import { useState } from "react";

import CodeWorkspace from "./components/CodeWorkspace";
import ExecutionConsole from "./components/ExecutionConsole";
import { useExecution } from "./hooks/useExecution";
import { INITIAL_SCRIPTS, LANGUAGES } from "./lib/languages";
import ResizableWorkspace from "./components/ResizableWorkspace";
import BackendStatus from "./components/BackendStatus";

export default function App() {
  const [language, setLanguage] = useState("python");
  const [scripts, setScripts] = useState(() => ({ ...INITIAL_SCRIPTS }));

  const execution = useExecution();

  const selectedLanguage = LANGUAGES.find(
    (item) => item.id === language,
  );

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
          Local development only. Evaluation deadlines are best-effort; hard memory isolation is not enabled.        
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
            disabled={
              execution.running ||
              !selectedLanguage.executable ||
              !scripts[language].trim()
            }
            onClick={runCurrentScript}
          >
            {execution.running ? "Running…" : `Run ${selectedLanguage.label}`}
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
    </main>
  );
}