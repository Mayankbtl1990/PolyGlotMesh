# PolyglotMesh Architecture

## Frontend

React and Monaco provide:
- Python, JavaScript, and Java editor tabs.
- Independent in-memory draft content.
- Execution and guest error consoles.
- Script library save/load/update.
- Mock product data binding.
- Runtime diagnostics and metrics.

Java is reference-only.

## Ordinary script execution

Browser
  -> POST /api/executions
  -> WebFlux controller
  -> Dedicated guest scheduler
  -> GuestRuntime
  -> Fresh restricted GraalVM Context
  -> Python or JavaScript

The Engine is shared.
Contexts are not reused between requests.

## Product-bound execution

Browser selects a product and submits a script.
Java queries an in-memory catalog.
Java constructs a pricing HashMap.
A controlled ProxyObject exposes permitted pricing fields.
Python or JavaScript updates finalPrice.
Java reads the updated map.

The catalog is a mock, not MongoDB.

Java-to-guest binding does not serialize the map as JSON.
Interop calls and numeric conversion still have costs.
Browser requests and responses use JSON.

## Script persistence

Browser
  -> /api/scripts
  -> Dedicated blocking storage scheduler
  -> JdbcTemplate
  -> File-backed H2

SQL values use prepared statement parameters.

Saving does not execute code.
Java reference files can be stored.

Current update behavior is last-write-wins.
There is no revision history or multi-user conflict control.

The local database location depends on the process working directory.
Use the backend directory consistently when starting the application.

## Metrics

Ordinary /api/executions calls are measured.

Pricing audit and product-bound execution are not included in the Week 3
aggregate guest metrics.

The mock REST baseline is only an artificial delay.
It is not an equivalent-workload benchmark.

## Security boundary

The application is a local development prototype.

Configured controls include:
- Restricted host access and class lookup.
- Disabled guest host filesystem/socket IO.
- Disabled process creation, thread creation, native access, and environment access.
- Bounded captured output.
- Best-effort evaluation cancellation.
- Bounded guest and storage scheduler capacity.

Not implemented:
- Hard guest memory limits.
- Hard CPU quotas.
- Authentication.
- Per-user script ownership.
- Public multi-tenant isolation.
- Comprehensive sandbox security verification.
- Durable audit logging.
- Storage quota enforcement.

A database must not be treated as safe storage for secrets submitted by users.
Do not store passwords, API keys, or confidential production data in scripts.

## Native Image

The Native Image profile is experimental.

Adding persistence changes the application dependency graph.
Any earlier native result must be revalidated against the final application.

The JVM application remains the working baseline unless the final native
build passes its required checks.

## Production follow-up

Before public deployment:
- Establish an explicit threat model.
- Use a suitable external isolation boundary for untrusted code.
- Add authentication and authorization.
- Add resource quotas, rate limits, storage limits, and observability.
- Review cross-origin and CSRF protections.
- Add migrations and backup/restore procedures.
- Verify dependency security and platform support.