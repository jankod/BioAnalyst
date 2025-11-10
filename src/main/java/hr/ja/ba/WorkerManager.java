package hr.ja.ba;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
@Data
public class WorkerManager {


    private Map<String, WorkerHolder> workers = new ConcurrentHashMap<>();

    private final WorkerStatus status = new WorkerStatus();

    @ShellMethod(key = "start")
    public String start(String workerName) {
        // start async worker as callable task and return immediately
        // callable with finish status update
        return "...";
    }

    @ShellMethod(key = "stop")
    public String stop(String workerName) {
        return "...";
    }

    @ShellMethod(key = "status")
    public String status(String workerName) {

        return "...";
    }

    @ShellMethod(key = "info")
    public String info(String workerName) {

        return "...";
    }



}
