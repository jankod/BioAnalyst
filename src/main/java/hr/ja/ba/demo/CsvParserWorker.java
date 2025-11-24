package hr.ja.ba.demo;

import hr.ja.ba.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BioWorker(name = "csv1", description = "Parse CSV for demo...", version = "1.0")
public class CsvParserWorker implements Worker, WorkerUI {

    @Override
    public WorkerResult run(WorkerContext context) {
        log.debug("working demo worker");


        if (context.isCancelled()) {
            return WorkerResult.cancelled();
        }

        for (int i = 0; i < 10; i++) {

            if (context.isCancelled()) {
                return WorkerResult.cancelled();
            }

            log.debug("demo worker dela {}", i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return WorkerResult.cancelled();
            }
        }
        log.debug("Finished demo worker");

        return new WorkerResult("Demo worker completed successfully.");
    }

    @Override
    public WorkerInput createInput() {
        return null;
    }
}
