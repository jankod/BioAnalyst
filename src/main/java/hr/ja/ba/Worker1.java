package hr.ja.ba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Demo worker that just sleeps and updates progress to showcase WorkerManager.
 */
@Component("worker1")
@Slf4j
public class Worker1 extends AbstractWorker {

    @Override
    protected WorkerResult doWork() throws Exception {
        log.info("Worker1 started working {}", getStatus().getId());
        int totalSteps = 10;
        updateProgress(0, totalSteps, "Preparing");

        for (int i = 1; i <= totalSteps; i++) {
            ensureRunning();
            try {
                Thread.sleep(200 + new Random().nextInt(800));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.info("Worker1 interrupted during sleep {}", getStatus().getId());
                throw WorkerStopSignal.interrupted();
            }
            updateProgress(i, "radim nesto");
        }

        log.info("Worker1 finished working {}", getStatus().getId());

        return new WorkerResult("Worker1 completed successfully.");
    }
}
