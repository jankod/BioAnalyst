package hr.ja.w3;

import lombok.Data;

@Data
public class TaskContext {

    private String currentMessage = "";
    private int progressPercent = 0;

    public void updateProgress(int percent) {

    }

    public boolean isCancelled() {
        return Thread.currentThread().isInterrupted();
    }
}
