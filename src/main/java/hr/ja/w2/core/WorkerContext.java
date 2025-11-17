package hr.ja.w2.core;

import java.util.Collections;
import java.util.Map;

public final class WorkerContext {

    private final String runId;
    private final Map<String, String> params;

    public WorkerContext(String runId, Map<String, String> params) {
        this.runId = runId;
        this.params = Collections.unmodifiableMap(params);
    }

    public String runId() {
        return runId;
    }

    public Map<String, String> params() {
        return params;
    }

    public String param(String key) {
        return params.get(key);
    }
}
