package hr.ja.ba;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class WorkerResult {

    private final String resultMessage;
    private Class<? extends AbstractWorker> callNextWorker;

    public WorkerResult(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public WorkerResult(Class<? extends AbstractWorker> callNextWorker, String resultMessage) {
        this.callNextWorker = callNextWorker;
        this.resultMessage = resultMessage;
    }

}
