package hr.ja.w2.shell;

import hr.ja.w2.core.WorkerDefinition;
import hr.ja.w2.core.WorkerExecutor;
import hr.ja.w2.core.WorkerRunInstance;
import hr.ja.w2.core.WorkerRunResult;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class W2Manager {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private final WorkerExecutor executor;

    @ShellMethod(key = "w2-workers", value = "Prikaži registrirane W2 workere")
    public String workers() {
        StringBuilder sb = new StringBuilder();
        for (WorkerDefinition definition : executor.definitions()) {
            sb.append("- ").append(definition.name()).append(": ").append(definition.description()).append("\n");
        }
        return sb.length() == 0 ? "Nema registriranih workera." : sb.toString();
    }

    @ShellMethod(key = "w2-start", value = "Pokreni W2 workera")
    public String start(
            @ShellOption(value = {"-w", "--worker"}) String workerName,
            @ShellOption(value = {"-p", "--params"}, defaultValue = "") String params
    ) {
        Map<String, String> parsed = parseParams(params);
        WorkerRunInstance run = executor.start(workerName, parsed);
        return "Pokrenut worker " + workerName + " s ID-om " + run.getId();
    }

    @ShellMethod(key = "w2-runs", value = "Prikaži aktivne W2 workere")
    public String runs() {
        List<WorkerRunInstance> runs = executor.activeRuns();
        if (runs.isEmpty()) {
            return "Nema aktivnih workera.";
        }
        return runs.stream()
                .map(run -> run.getId() + " :: " + run.getDefinition().name() + " :: " + run.getStatus())
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "w2-history", value = "Prikaži povijest završenih W2 runova")
    public String history() {
        List<WorkerRunInstance> history = executor.history();
        if (history.isEmpty()) {
            return "Još nema završenih runova.";
        }
        return history.stream()
                .map(this::formatHistoryLine)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "w2-stop", value = "Zaustavi W2 run")
    public String stop(@ShellOption(value = {"-r", "--run"}) String runId) {
        boolean stopped = executor.stop(runId);
        return stopped ? "Zaustavljen run " + runId : "Run " + runId + " nije aktivan.";
    }

    @ShellMethod(key = "w2-info", value = "Prikaži detalje runa")
    public String runInfo(@ShellOption(value = {"-r", "--run"}) String runId) {
        WorkerRunInstance run = executor.requireRun(runId);
        StringBuilder sb = new StringBuilder();
        sb.append("Worker: ").append(run.getDefinition().name()).append("\n");
        sb.append("Opis: ").append(run.getDefinition().description()).append("\n");
        sb.append("Status: ").append(run.getStatus()).append("\n");
        sb.append("Pokrenut: ").append(formatInstant(run.getStartedAt())).append("\n");
        sb.append("Završen: ").append(formatInstant(run.getFinishedAt())).append("\n");
        sb.append("Parametri:\n");
        run.getParams().forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append("\n"));

        WorkerRunResult result = run.getResult();
        if (result != null) {
            sb.append("Output:\n");
            result.output().forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append("\n"));
            if (!result.success()) {
                sb.append("Error: ").append(result.errorMessage()).append("\n");
            }
        }
        return sb.toString();
    }

    private String formatHistoryLine(WorkerRunInstance run) {
        String finished = formatInstant(run.getFinishedAt());
        WorkerRunResult result = run.getResult();
        String outcome = result == null ? "-" : (result.success() ? "OK" : "FAIL");
        return run.getId() + " :: " + run.getDefinition().name() + " :: " + outcome + " :: " + finished;
    }

    private String formatInstant(java.time.Instant instant) {
        return instant == null ? "-" : ISO.format(instant);
    }

    private Map<String, String> parseParams(String raw) {
        Map<String, String> params = new LinkedHashMap<>();
        if (!StringUtils.hasText(raw)) {
            return params;
        }

        String[] pairs = raw.split(",");
        for (String pair : pairs) {
            String trimmed = pair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int idx = trimmed.indexOf('=');
            if (idx <= 0) {
                throw new IllegalArgumentException("Parametar mora biti oblika key=value: " + trimmed);
            }
            String key = trimmed.substring(0, idx).trim();
            String value = trimmed.substring(idx + 1).trim();
            params.put(key, value);
        }
        return params;
    }
}
