export default function ScriptLibraryView({
  scripts,
  name,
  onNameChange,
  selectedId,
  onSelect,
  onSaveNew,
  onUpdate,
  onLoad,
  busy,
  canSave,
  canUpdate,
  message,
  error,
}) {
  return (
    <section className="panel library-panel" aria-label="Saved script library">
      <h2>Saved Script Library</h2>

      <p className="muted">
        Save one language tab at a time. Java files can be stored as reference
        code but cannot be executed.
      </p>

      <div className="library-controls">
        <label>
          Script name
          <input
            value={name}
            maxLength={80}
            disabled={busy}
            onChange={(event) => onNameChange(event.target.value)}
            placeholder="Dynamic pricing"
          />
        </label>

        <button
          type="button"
          className="secondary-button"
          disabled={busy || !canSave}
          onClick={onSaveNew}
        >
          Save as new
        </button>

        <button
          type="button"
          className="secondary-button"
          disabled={busy || !canSave || !canUpdate}
          onClick={onUpdate}
        >
          Update loaded script
        </button>
      </div>

      <div className="library-controls">
        <label>
          Saved scripts
          <select
            value={selectedId}
            disabled={busy}
            onChange={(event) => onSelect(event.target.value)}
          >
            <option value="">Select a saved script</option>

            {scripts.map((script) => (
              <option key={script.id} value={script.id}>
                {script.name} — {script.language}
              </option>
            ))}
          </select>
        </label>

        <button
          type="button"
          className="secondary-button"
          disabled={busy || !selectedId}
          onClick={onLoad}
        >
          Load selected script
        </button>
      </div>

      {busy && <p role="status">Working…</p>}
      {message && <p role="status">{message}</p>}
      {error && <p role="alert" className="console-error">{error}</p>}
    </section>
  );
}