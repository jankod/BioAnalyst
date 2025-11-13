package hr.ja.ba;

/**
 * Internal stop signal used to unwind worker execution without treating it as an error.
 */
public class WorkerStopSignal extends RuntimeException {

    private final String reason;

    private WorkerStopSignal(String reason) {
        super(reason);
        this.reason = reason;
    }

    public static WorkerStopSignal cancelled() {
        return new WorkerStopSignal("Cancelled");
    }

    public static WorkerStopSignal interrupted() {
        return new WorkerStopSignal("Interrupted");
    }

    public String getReason() {
        return reason;
    }
}
