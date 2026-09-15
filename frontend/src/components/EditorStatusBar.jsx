export default function EditorStatusBar({ language, code }) {
  const lines = code.split("\n").length;

  return (
    <footer className="status-bar" aria-label="Editor status">
      <span>Language: {language}</span>
      <span>Lines: {lines}</span>
      <span>Characters: {code.length.toLocaleString()} / 20,000</span>
      <span>Storage: session memory only</span>
    </footer>
  );
}