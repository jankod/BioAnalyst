package hr.ja.ba;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base helper for background workers so every worker reports progress in the same way.
 */
@Slf4j
public abstract class AbstractWorker implements Runnable {

    private final AtomicBoolean cancelRequested = new AtomicBoolean(false);

    @Getter
    private final WorkerStatus status = new WorkerStatus(WorkerIdGenerator.getNextId());

    public synchronized void reset() {
        cancelRequested.set(false);
        status.setRunning(false);
        status.setCancelled(false);
        status.setProcessed(0);
        status.setTotal(0);
        status.setMessage("Pending");
    }

    public synchronized void cancel() {
        cancelRequested.set(true);
        status.setCancelled(true);
        status.setMessage("Cancellation requested");
    }

    protected boolean isCancelled() {
        return cancelRequested.get();
    }

    protected synchronized void updateProgress(long processed, long total, String message) {
        status.setProcessed(processed);
        status.setTotal(total);
        status.setMessage(message);
    }

    protected synchronized void updateProgress(long processed, String message) {
        status.setProcessed(processed);
        status.setMessage(message);
    }

    protected void ensureRunning() {
        if (isCancelled()) {
            throw WorkerStopSignal.cancelled();
        }
        if (Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
            throw WorkerStopSignal.interrupted();
        }
    }

    @Override
    public final void run() {
        status.setRunning(true);
        try {
            WorkerResult result = doWork();
            status.setResult(result);
            if (isCancelled()) {
                status.setMessage("Cancelled");
            } else {
                status.setMessage("Completed");
            }
        } catch (WorkerStopSignal stopSignal) {
            String reason = stopSignal.getReason();
            status.setResult(new WorkerResult("Stopped: " + reason));
            status.setMessage(reason);
            log.info("Worker {} stopped: {}", status.getId(), reason);
        } catch (Exception ex) {
            status.setMessage("Failed: " + ex.getMessage());
            throw new IllegalStateException("Worker failed", ex);
        } finally {
            status.setRunning(false);
        }
    }

    protected abstract WorkerResult doWork() throws Exception;

}
