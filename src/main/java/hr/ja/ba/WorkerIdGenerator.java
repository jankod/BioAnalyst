package hr.ja.ba;

public class WorkerIdGenerator {
    private static long currentId = 0;

    public static long getNextId() {
        return ++currentId;
    }
}
