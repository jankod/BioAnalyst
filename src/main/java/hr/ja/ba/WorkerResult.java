package hr.ja.ba;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorkerResult {

    private final String message;
    private final Map<String, String> results = new HashMap<>();

    public WorkerResult(String message) {
        this.message = message;
    }

    public static WorkerResult cancelled() {
        return null;
    }
}
