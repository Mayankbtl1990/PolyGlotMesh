import { useState } from "react";

import CodeWorkspace from "./components/CodeWorkspace";
import ExecutionConsole from "./components/ExecutionConsole";
import { useExecution } from "./hooks/useExecution";
import { INITIAL_SCRIPTS, LANGUAGES } from "./lib/languages";

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

        <span className="muted">Week 1 · Local prototype</span>
      </header>

      <div className="toolbar">
        <p className="muted">
          Trusted scripts only. Execution cancellation is not enabled yet.
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

      <div className="workspace">
        <CodeWorkspace
          language={language}
          code={scripts[language]}
          onLanguageChange={changeLanguage}
          onCodeChange={updateCode}
          readOnly={execution.running}
        />

        <ExecutionConsole execution={execution} />
      </div>
    </main>
  );
}