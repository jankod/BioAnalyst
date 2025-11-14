package hr.ja.ba;

import java.util.concurrent.atomic.AtomicLong;

public class WorkerIdGenerator {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    public static long getNextId() {
        return idGenerator.incrementAndGet();
    }
}
