import ConsolePanel from "./components/ConsolePanel";

export default function App() {
  return (
    <main className="app-shell">
      <header className="app-header">
        <div>
          <h1>PolyglotMesh</h1>
          <p>One workspace. Multiple languages.</p>
        </div>
      </header>

      <div className="workspace">
        <section className="panel">
          <h2>Code Editor</h2>
          <p>The Monaco editor will be added on Day 2.</p>
        </section>

        <ConsolePanel />
      </div>
    </main>
  );
}