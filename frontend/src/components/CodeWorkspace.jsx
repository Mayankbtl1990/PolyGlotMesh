import Editor from "@monaco-editor/react";
import { LANGUAGES } from "../lib/languages";

export default function CodeWorkspace({
  language,
  code,
  onLanguageChange,
  onCodeChange,
  readOnly = false,
}) {
  const selected = LANGUAGES.find((item) => item.id === language);

  return (
    <section className="panel" aria-label="Code workspace">
      <h2>Code Editor</h2>

      <div className="tabs" role="group" aria-label="Editor language">
        {LANGUAGES.map((item) => (
          <button
            key={item.id}
            type="button"
            className={`tab ${language === item.id ? "active" : ""}`}
            aria-pressed={language === item.id}
            disabled={readOnly}
            onClick={() => onLanguageChange(item.id)}
          >
            {item.label}
          </button>
        ))}
      </div>

      {!selected.executable && (
        <p className="warning">
          Java is reference-only. Run Python or JavaScript scripts.
        </p>
      )}

      <Editor
        height="480px"
        path={`script.${selected.extension}`}
        language={language}
        theme="vs-dark"
        value={code}
        onChange={(value) => onCodeChange(value ?? "")}
        options={{
          readOnly,
          minimap: { enabled: false },
          fontSize: 14,
          automaticLayout: true,
          scrollBeyondLastLine: false,
          tabSize: 4,
        }}
      />
    </section>
  );
}