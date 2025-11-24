package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class WorkerShell {

    private final WorkerRegistry workerRegistry;
    private final WorkerManager workerManager;

    @ShellMethod(key = "list", value = "List all workers")
    public String list() {
        return workerRegistry.findAll()
                .stream()
                .map(descriptor -> descriptor.name() + " - " + descriptor.description() + " (v" + descriptor.version() + ")")
                .sorted()
                .reduce((a, b) -> a + System.lineSeparator() + b)
                .orElse("Not found any workers.");
    }

    @ShellMethod(key = {"start", "s"}, value = "Start worker by name")
    public String start(String workerName) {
        Optional<WorkerRegistry.WorkerDescriptor> workerOpt = workerRegistry.findByName(workerName);
        if (workerOpt.isEmpty()) {
            return "Worker with name '" + workerName + "' not found.";
        }

        workerManager.executeWorker(workerOpt.get().worker());
        return "Worker '" + workerName + "' started.";

    }

}
