package hr.ja.ba;

import org.springframework.shell.command.annotation.Command;

@Command(command = "worker", description = "Worker related commands")
public class WorkerShell {

    public String list() {
        return "Listing workers...";
    }
}
