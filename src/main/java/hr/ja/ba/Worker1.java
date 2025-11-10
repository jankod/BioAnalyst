package hr.ja.ba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Demo worker that just sleeps and updates progress to showcase WorkerManager.
 */
@Component("worker1")
@Slf4j
public class Worker1 extends AbstractWorker {

    @Override
    protected void doWork() throws Exception {
        log.info("Worker1 started working "+ getStatus().getId());
        int totalSteps = 10;
        updateProgress(0, totalSteps, "Preparing");

        for (int i = 1; i <= totalSteps; i++) {
            if (isCancelled()) {
                return;
            }
            Thread.sleep(200);
            updateProgress(i, totalSteps, "Step " + i + "/" + totalSteps);
        }

        log.info("Worker1 finished working "+ getStatus().getId());
    }
}
