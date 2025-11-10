package hr.ja.ba;

import lombok.Data;

@Data
public class WorkerStatus {

    private final long id;
    private volatile boolean running;
    private volatile boolean cancelled;
    private volatile long processed;
    private volatile long total;
    private volatile String message;

    public WorkerStatus(long id) {
        this.id = id;
    }

    public String getProgressInfo() {
        long proc = getProcessed();
        long total = getTotal();
        double pct = total > 0 ? (proc * 100.0 / total) : 0;

        StringBuilder bar = new StringBuilder("[");
        int filled = (int) (pct / 5);
        for (int i = 0; i < 20; i++) {
            bar.append(i < filled ? "=" : " ");
        }
        bar.append("] ");

        return bar + String.format("%.1f%% | %d/%d | %s",
              pct, proc, total, getMessage());
    }
}
