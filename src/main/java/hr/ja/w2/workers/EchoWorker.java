package hr.ja.w2.workers;

import hr.ja.w2.annotation.BioWorker;
import hr.ja.w2.annotation.Run;
import hr.ja.w2.core.WorkerContext;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@BioWorker(name = "echo", description = "Demo worker that echoes the provided message")
public class EchoWorker {

    @Run(requiredParams = {"message"})
    public Map<String, String> run(WorkerContext context) throws InterruptedException {
        String message = context.param("message");
        Instant started = Instant.now();
        Thread.sleep(500); // simulate some processing
        return Map.of(
                "runId", context.runId(),
                "message", message,
                "uppercase", message.toUpperCase(),
                "durationMs", String.valueOf(Duration.between(started, Instant.now()).toMillis())
        );
    }
}
