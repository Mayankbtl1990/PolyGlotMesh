import { useEffect, useRef, useState } from "react";

import {
  createSavedScript,
  getSavedScript,
  listSavedScripts,
  updateSavedScript,
} from "../lib/api";

import ScriptLibraryView from "./ScriptLibraryView";

export default function ScriptLibraryPanel({
  language,
  code,
  disabled = false,
  onLoad,
  onBusyChange,
}) {
  const inFlight = useRef(false);

  const [scripts, setScripts] = useState([]);
  const [initialLoading, setInitialLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [name, setName] = useState("");
  const [selectedId, setSelectedId] = useState("");
  const [loaded, setLoaded] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    listSavedScripts()
      .then((response) => {
        if (active) {
          setScripts(response);
        }
      })
      .catch((failure) => {
        if (active) {
          setError(failure.message || "Cannot load the script library.");
        }
      })
      .finally(() => {
        if (active) {
          setInitialLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  async function perform(operation) {
    if (inFlight.current || disabled || initialLoading) {
      return;
    }

    inFlight.current = true;
    setBusy(true);
    onBusyChange(true);
    setError("");
    setMessage("");

    try {
      await operation();
    } catch (failure) {
      setError(failure.message || "Script library operation failed.");
    } finally {
      inFlight.current = false;
      setBusy(false);
      onBusyChange(false);
    }
  }

  function remember(script) {
    const summary = {
      id: script.id,
      name: script.name,
      language: script.language,
      updatedAt: script.updatedAt,
    };

    setScripts((current) => [
      summary,
      ...current.filter((item) => item.id !== script.id),
    ]);

    setLoaded(script);
    setSelectedId(script.id);
    setName(script.name);
  }

  function saveNew() {
    return perform(async () => {
      const saved = await createSavedScript({
        name: name.trim(),
        language,
        code,
      });

      remember(saved);
      setMessage(`Saved "${saved.name}" as a new script.`);
    });
  }

  function updateLoaded() {
    if (!loaded || loaded.language !== language) {
      return;
    }

    if (!window.confirm(`Replace the saved content of "${loaded.name}"?`)) {
      return;
    }

    return perform(async () => {
      const saved = await updateSavedScript(loaded.id, {
        name: name.trim(),
        language,
        code,
      });

      remember(saved);
      setMessage(`Updated "${saved.name}".`);
    });
  }

  function loadSelected() {
    return perform(async () => {
      const script = await getSavedScript(selectedId);

      const accepted = onLoad(script);

      if (accepted === false) {
        setMessage("Loading cancelled. Editor content was not replaced.");
        return;
      }

      remember(script);
      setMessage(`Loaded "${script.name}".`);
    });
  }

  const canSave =
    Boolean(name.trim()) &&
    Boolean(code.trim()) &&
    code.length <= 20_000;

  return (
    <ScriptLibraryView
      scripts={scripts}
      name={name}
      onNameChange={setName}
      selectedId={selectedId}
      onSelect={setSelectedId}
      onSaveNew={saveNew}
      onUpdate={updateLoaded}
      onLoad={loadSelected}
      busy={busy || initialLoading || disabled}
      canSave={canSave}
      canUpdate={Boolean(loaded && loaded.language === language)}
      message={message}
      error={error}
    />
  );
}