package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class WorkerShell {

    private final WorkerRegistry workerRegistry;

    @ShellMethod(key = "list", value = "List all workers")
    public String list() {
        return workerRegistry.findAll()
                .stream()
                .map(descriptor -> descriptor.name() + " - " + descriptor.description() + " (v" + descriptor.version() + ")")
                .sorted()
                .reduce((a, b) -> a + System.lineSeparator() + b)
                .orElse("Nema registriranih workera.");
    }

}
