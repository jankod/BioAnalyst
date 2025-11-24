package hr.ja.ba;

import hr.ja.ba.db.WorkerDb;
import hr.ja.ba.db.WorkerDbRepository;
import hr.ja.ba.db.WorkerStatusDb;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerManager {

    @Qualifier("bioTaskExecutor")
    private final AsyncTaskExecutor taskExecutor;

    private final WorkerDbRepository workerRepository;

    @Async
    public void executeWorker(Worker worker) {

        WorkerDb workerDb = new WorkerDb();
        workerDb.setStatus(WorkerStatusDb.PENDING);
        workerRepository.saveAndFlush(workerDb);

        WorkerContext workerContext = new WorkerContext(workerDb.getId());
        CompletableFuture<WorkerResult> completableFuture = taskExecutor.submitCompletable(new WorkerHolder(worker, workerContext));
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                // Handle exception
                log.debug("Worker execution failed: " + throwable.getMessage());
            } else {
                // Process result
                log.debug("Worker completed with result: " + result);
            }
        });

    }

    @Data
    static class WorkerHolder implements Callable<WorkerResult> {

        private final Worker worker;
        private final WorkerContext context;

        private Instant started;
        private Instant finished;

        public WorkerHolder(Worker worker, WorkerContext context) {
            this.worker = worker;
            this.context = context;
        }

        @Override
        public WorkerResult call() {

            try {
                started = Instant.now();
                return worker.run(context);
            } finally {
                finished = Instant.now();
            }
        }
    }

}
