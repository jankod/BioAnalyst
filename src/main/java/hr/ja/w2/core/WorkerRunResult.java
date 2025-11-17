package hr.ja.w2.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public record WorkerRunResult(
        boolean success,
        Map<String, String> output,
        String errorMessage,
        Instant startedAt,
        Instant finishedAt
) {

    public static WorkerRunResult success(Map<String, String> output, Instant startedAt, Instant finishedAt) {
        Map<String, String> safeOutput = output == null ? Map.of() : Collections.unmodifiableMap(output);
        return new WorkerRunResult(true, safeOutput, null, startedAt, finishedAt);
    }

    public static WorkerRunResult failure(String errorMessage, Instant startedAt, Instant finishedAt) {
        return new WorkerRunResult(false, Map.of(), errorMessage, startedAt, finishedAt);
    }
}
