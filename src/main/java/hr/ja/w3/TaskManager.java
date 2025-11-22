package hr.ja.w3;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TaskManager {

    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private Map<String, Future<?>> runningTasks = new ConcurrentHashMap<>();


    /**
     * Runs a task of the given class in a new virtual thread.
     *
     * @param taskClass class of the task to run
     * @return taskId
     */
    public int runTask(Class<? extends BioTask> taskClass) {

    }

    public void runTask(String taskId, BioTask task) {
        Future<?> future = executor.submit(() -> {
            // Ovdje se izvršava task.execute(input, context)
            task.execute(input, context);
        });
        runningTasks.put(taskId, future);
    }

    public void cancelTask(String taskId) {
        Future<?> future = runningTasks.get(taskId);
        if (future != null) {
            // Pokušava otkazati izvršavanje. Argument 'true' označava da se dretva može prekinuti (interrupt).
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                System.out.println("Task " + taskId + " je zatražen za otkazivanje.");
            }
        }
    }


}
