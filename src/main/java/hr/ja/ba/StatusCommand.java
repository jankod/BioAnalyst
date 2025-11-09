package hr.ja.ba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command(
      name = "status",
      description = "Prikazuje status odredjene analize."
)
@Slf4j
@Component
public class StatusCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Naziv taska.")
    private String task;

    @Override
    public void run() {
        log.debug("StatusCommand.run");
        log.info("StatusCommand.run info");
    }
}
