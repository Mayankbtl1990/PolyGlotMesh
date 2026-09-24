# Native Image Compatibility Investigation

## Status

Experimental. No successful native execution is claimed by this document.

The working baseline remains the JVM application.

## Required toolchain

- A GraalVM JDK with the native-image command.
- A supported OS/architecture for the selected language runtimes.
- Native compiler toolchain required by that GraalVM installation.
- Sufficient build RAM and disk space.

Check:

    java -version
    mvn -version
    native-image --version

The Week 1 Java 21 and Polyglot 24.1.2 combination is the JVM baseline.
Native Image may require a different, explicitly compatible toolchain.

If versions must change:
- Change them on a dedicated branch.
- Keep Polyglot API and language artifact versions aligned.
- Run all JVM tests again.
- Record the exact versions and platform.

Do not silently change the team's working baseline.

## Attempt the build

From backend:

    mvn -Pnative-prototype -DskipTests clean package

Tests must be run separately before this build.
Skipping tests here does not demonstrate native correctness.

## Success criteria

All of these must pass:

1. Native executable is produced.
2. Application starts.
3. Health endpoint responds.
4. Diagnostics reports NATIVE_IMAGE.
5. Python executes successfully.
6. JavaScript executes successfully.
7. Pricing interoperability audit succeeds.
8. Restrictions and cancellation are revalidated on the native build.

A successful compilation or health endpoint alone is not enough.

## Failure handling

Record the exact stage and first relevant error.

Possible stages:
- Spring AOT processing.
- Native Image analysis.
- Native linking.
- Application startup.
- Language initialization.
- Guest execution.
- Cancellation or interoperability.

Do not hide build failures or label the JVM application as native.

If embedded Python is unsupported with the selected combination, keep the
JVM deployment and document the blocker.