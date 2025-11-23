package hr.ja.ba;

public class WorkerResult {

    private final String message;

    public WorkerResult(String message) {
        this.message = message;
    }

    public static WorkerResult cancelled() {
        return null;
    }
}
