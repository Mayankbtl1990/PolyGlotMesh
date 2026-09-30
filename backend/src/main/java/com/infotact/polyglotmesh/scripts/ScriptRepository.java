package com.infotact.polyglotmesh.scripts;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScriptRepository {

    private final JdbcTemplate jdbc;

    public ScriptRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SavedScript create(
            String name,
            String language,
            String code
    ) {
        SavedScript script = new SavedScript(
                UUID.randomUUID().toString(),
                name.strip(),
                language,
                code,
                Instant.now().toString()
        );

        jdbc.update(
                """
                INSERT INTO saved_scripts
                    (id, name, language, code, updated_at)
                VALUES (?, ?, ?, ?, ?)
                """,
                script.id(),
                script.name(),
                script.language(),
                script.code(),
                script.updatedAt()
        );

        return script;
    }

    public List<ScriptSummary> list() {
        return jdbc.query(
                """
                SELECT id, name, language, updated_at
                FROM saved_scripts
                ORDER BY updated_at DESC, id
                """,
                (row, rowNumber) -> new ScriptSummary(
                        row.getString("id"),
                        row.getString("name"),
                        row.getString("language"),
                        row.getString("updated_at")
                )
        );
    }

    public SavedScript find(String id) {
        List<SavedScript> scripts = jdbc.query(
                """
                SELECT id, name, language, code, updated_at
                FROM saved_scripts
                WHERE id = ?
                """,
                (row, rowNumber) -> new SavedScript(
                        row.getString("id"),
                        row.getString("name"),
                        row.getString("language"),
                        row.getString("code"),
                        row.getString("updated_at")
                ),
                id
        );

        if (scripts.isEmpty()) {
            throw new ScriptNotFoundException();
        }

        return scripts.getFirst();
    }

    public SavedScript update(
            String id,
            String name,
            String language,
            String code
    ) {
        SavedScript script = new SavedScript(
                id,
                name.strip(),
                language,
                code,
                Instant.now().toString()
        );

        int affectedRows = jdbc.update(
                """
                UPDATE saved_scripts
                SET name = ?, language = ?, code = ?, updated_at = ?
                WHERE id = ?
                """,
                script.name(),
                script.language(),
                script.code(),
                script.updatedAt(),
                script.id()
        );

        if (affectedRows == 0) {
            throw new ScriptNotFoundException();
        }

        return script;
    }
}