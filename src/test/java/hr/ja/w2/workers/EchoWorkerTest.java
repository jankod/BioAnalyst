package hr.ja.w2.workers;

import hr.ja.ba.App;
import hr.ja.w2.core.WorkerExecutor;
import hr.ja.w2.core.WorkerRunInstance;
import hr.ja.w2.core.WorkerRunResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = App.class)
@EnableAsync
class EchoWorkerTest {

    @Autowired
    private WorkerExecutor workerExecutor;

    @Test
    void echoWorkerReturnsUppercaseMessage() throws Exception {
        WorkerRunInstance run = workerExecutor.start("echo", Map.of("message", "bio"));
        WorkerRunResult result = awaitResult(run);

        assertTrue(result.success(), "Worker run should complete successfully");
        assertEquals("bio", result.output().get("message"));
        assertEquals("BIO", result.output().get("uppercase"));
        assertNotNull(result.output().get("runId"));
    }

    private WorkerRunResult awaitResult(WorkerRunInstance run) throws InterruptedException {
        long timeoutMs = Duration.ofSeconds(5).toMillis();
        long waited = 0L;
        while (run.getResult() == null && waited < timeoutMs) {
            Thread.sleep(50);
            waited += 50;
        }
        WorkerRunResult result = run.getResult();
        assertNotNull(result, "Worker run did not finish in time");
        return result;
    }
}
