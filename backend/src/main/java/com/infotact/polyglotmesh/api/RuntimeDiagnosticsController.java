package com.infotact.polyglotmesh.api;

import com.infotact.polyglotmesh.runtime.RuntimeDiagnostics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
public class RuntimeDiagnosticsController {

    private final RuntimeDiagnostics diagnostics;

    public RuntimeDiagnosticsController(RuntimeDiagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    @GetMapping("/diagnostics")
    public RuntimeDiagnostics.Snapshot diagnostics() {
        return diagnostics.snapshot();
    }
}