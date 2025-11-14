package hr.ja.ba;

import lombok.Data;

import java.text.MessageFormat;

@Data
public class WorkerStatus {

    private final long id;
    private volatile boolean running;
    private volatile boolean cancelled;
    private volatile long processed;
    private volatile long total;
    private volatile String message;
    private volatile WorkerResult result;


    public WorkerStatus(long id) {
        this.id = id;
    }


    public String toString() {
        return MessageFormat.format("WorkerStatus id={0}, running={1}, cancelled={2}, processed={3}, total={4}, message=''{5}'', result=''{6}'' \n{7}",
              id, running, cancelled, processed, total, message, result, getProgressInfo());
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
