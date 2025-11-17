package hr.ja.w2.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;

public class WorkerRunInstance {

    private final String id;
    private final WorkerDefinition definition;
    private final Map<String, String> params;
    private final Instant createdAt = Instant.now();

    private volatile Instant startedAt;
    private volatile Instant finishedAt;
    private volatile WorkerRunStatus status = WorkerRunStatus.CREATED;
    private volatile WorkerRunResult result;
    private volatile Future<?> future;

    public WorkerRunInstance(String id, WorkerDefinition definition, Map<String, String> params) {
        this.id = id;
        this.definition = definition;
        this.params = Collections.unmodifiableMap(params);
    }

    public String getId() {
        return id;
    }

    public WorkerDefinition getDefinition() {
        return definition;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public WorkerRunStatus getStatus() {
        return status;
    }

    public WorkerRunResult getResult() {
        return result;
    }

    public Future<?> getFuture() {
        return future;
    }

    public boolean isActive() {
        return status == WorkerRunStatus.CREATED || status == WorkerRunStatus.RUNNING;
    }

    public void attachFuture(Future<?> future) {
        this.future = future;
    }

    public void markRunning() {
        this.status = WorkerRunStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void markCompleted(WorkerRunResult result) {
        this.status = WorkerRunStatus.COMPLETED;
        this.finishedAt = result.finishedAt();
        this.result = result;
    }

    public void markFailed(WorkerRunResult result) {
        this.status = WorkerRunStatus.FAILED;
        this.finishedAt = result.finishedAt();
        this.result = result;
    }

    public void markCancelled() {
        this.status = WorkerRunStatus.CANCELLED;
        this.finishedAt = Instant.now();
        this.result = new WorkerRunResult(false, Map.of(), "Cancelled by user", startedAt, finishedAt);
    }
}
