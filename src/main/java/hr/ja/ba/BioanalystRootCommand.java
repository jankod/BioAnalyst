package hr.ja.ba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@CommandLine.Command(
    name = "bioanalyst",
    mixinStandardHelpOptions = true,
    subcommands = {
        TaxIdCsvCompact.class,
        StatusCommand.class,
    }
)
@Slf4j
@Component
public class BioanalystRootCommand implements Runnable {
    @Override
    public void run() {
        // Nothing here – if user runs `bioanalyst` without args, just show help
        log.info("Bioanalyst command running");
        log.debug("Bioanalyst command running");
    }
}
