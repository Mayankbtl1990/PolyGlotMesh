package com.infotact.polyglotmesh.scripts;

public class ScriptNotFoundException extends RuntimeException {

    public ScriptNotFoundException() {
        super("Saved script was not found.");
    }
}