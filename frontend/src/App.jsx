import { useState } from "react";

import CodeWorkspace from "./components/CodeWorkspace";
import ConsolePanel from "./components/ConsolePanel";
import { INITIAL_SCRIPTS } from "./lib/languages";

export default function App() {
  const [language, setLanguage] = useState("python");
  const [scripts, setScripts] = useState(() => ({ ...INITIAL_SCRIPTS }));

  function updateCode(code) {
    setScripts((current) => ({
      ...current,
      [language]: code,
    }));
  }

  return (
    <main className="app-shell">
      <header className="app-header">
        <div>
          <h1>PolyglotMesh</h1>
          <p>One workspace. Multiple languages.</p>
        </div>
      </header>

      <div className="workspace">
        <CodeWorkspace
          language={language}
          code={scripts[language]}
          onLanguageChange={setLanguage}
          onCodeChange={updateCode}
        />

        <ConsolePanel />
      </div>
    </main>
  );
}