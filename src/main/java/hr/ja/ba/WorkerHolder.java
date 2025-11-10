package hr.ja.ba;

import lombok.Getter;

import java.time.Instant;
import java.util.concurrent.Future;

@Getter
public class WorkerHolder {

    private final String name;
    private final AbstractWorker worker;
    private final Instant startedAt;
    private volatile Instant finishedAt;
    private volatile Future<?> future;

    public WorkerHolder(String name, AbstractWorker worker, Instant startedAt) {
        this.name = name;
        this.worker = worker;
        this.startedAt = startedAt;
    }

    public void attachFuture(Future<?> future) {
        this.future = future;
    }

    public boolean isActive() {
        Future<?> ref = future;
        return ref != null && !ref.isDone();
    }

    public void markFinished() {
        finishedAt = Instant.now();
    }
}
