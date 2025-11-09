package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(
      name = "taxid1",
      description = "Kompaktira taxid->peptide CSV u RocksDB."
)
@Slf4j
@Component
@RequiredArgsConstructor
public class TaxIdCsvCompact implements Runnable {

    @CommandLine.Option(names = "--input", required = true)
    private File input;

    @CommandLine.Option(names = "--output", required = true)
    private File output;

    @Override
    public void run() {
        long pid = ProcessHandle.current().pid();
        log.debug("this work taxid command, pid: " + pid);
    }
}
