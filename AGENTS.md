# Repository Guidelines

## Project Structure & Module Organization
The CLI lives in `src/main/java/hr/ja/ba`, with `App` bootstrapping Spring Shell and discrete commands such as `BioanalystRootCommand`, `StatusCommand`, and `TaxIdCsvCompact`. Configuration, logging, and sample dataset paths are in `src/main/resources` (`application.properties`, overrides like `application_bioserv.properties`, and `logback-spring.xml`, which writes `bioanalyst.log` to the repo root). Keep future domain modules under `hr.ja.ba.<feature>` and mirror that layout in `src/test/java`, even though the test tree is currently empty.

## Build, Test, and Development Commands
- `./mvnw clean verify` — full compile plus unit/integration tests; run before any PR.
- `./mvnw spring-boot:run -Dspring-boot.run.arguments="status demo-task"` — launches the shell locally with inline arguments for fast iteration.
- `java -jar target/ba-0.0.1-SNAPSHOT.jar --spring.config.additional-location=/etc/bioanalyst/ taxid1 --input data.csv --output store.db` — exercises the packaged artifact while pointing at ops-configured property files.  
Logback rotates daily, so tail `bioanalyst.log` when validating new commands.

## Coding Style & Naming Conventions
Use Java 25, 4-space indentation, and Lombok to avoid manual boilerplate. Place every command in the `hr.ja.ba` package, name classes after their shell verb (`StatusCommand`, `TaxIdCsvCompact`), and annotate with `@CommandLine.Command`. Keep options lowercase long-form flags (`--input`, `--output`) and prefer SLF4J parameterized logs over string concatenation for runtime traces. Update `logback-spring.xml` rather than ad-hoc logging tweaks in code.

## Testing Guidelines
JUnit 5 (via `spring-boot-starter-test`) is available. Co-locate tests under `src/test/java/hr/ja/ba`, name files `ClassNameTest`, and use Spring Shell’s test utilities when verifying command wiring. Cover nominal runs plus error output (e.g., missing parameters) and ensure each new command includes at least one test that asserts exit codes. Execute `./mvnw test` locally; add `./mvnw verify -Pci` if you introduce extra Maven profiles.

## Commit & Pull Request Guidelines
Commits should be small, imperative, and scoped (`feat: add status command logging`). Reference issue IDs when available and include relevant CLI samples in the body. For PRs, describe the feature, note configuration changes (paths in `application*.properties`, logback tweaks), attach console snippets for new commands, and list tests run. Request at least one reviewer familiar with the touched command module.

## Security & Configuration Tips
Never hard-code dataset paths—extend the `application*.properties` family or rely on `--spring.config.additional-location`. Treat CSV inputs as untrusted: validate before compacting into RocksDB and document expected file shapes inside the command description strings. Rotate credentials or tokens by editing resource files, not Java sources.

## Odgovaraj mi na hrvatskom, kod pisi na engleskom
