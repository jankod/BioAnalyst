package hr.ja.ba;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base helper for background workers so every worker reports progress in the same way.
 */
public abstract class AbstractWorker implements Runnable {

    private final AtomicBoolean cancelRequested = new AtomicBoolean(false);

    @Getter
    private final WorkerStatus status = new WorkerStatus(WorkerIdGenerator.getNextId());

    public void reset() {
        cancelRequested.set(false);
        status.setRunning(false);
        status.setCancelled(false);
        status.setProcessed(0);
        status.setTotal(0);
        status.setMessage("Pending");
    }

    public void cancel() {
        cancelRequested.set(true);
        status.setCancelled(true);
        status.setMessage("Cancellation requested");
    }

    protected boolean isCancelled() {
        return cancelRequested.get();
    }

    protected void updateProgress(long processed, long total, String message) {
        status.setProcessed(processed);
        status.setTotal(total);
        status.setMessage(message);
    }

    @Override
    public final void run() {
        status.setRunning(true);
        try {
            doWork();
            if (isCancelled()) {
                status.setMessage("Cancelled");
            } else {
                status.setMessage("Completed");
            }
        } catch (Exception ex) {
            status.setMessage("Failed: " + ex.getMessage());
            throw new IllegalStateException("Worker failed", ex);
        } finally {
            status.setRunning(false);
        }
    }

    protected abstract void doWork() throws Exception;


}
