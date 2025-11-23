package hr.ja.ba;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadConfig {

    @Bean("bioTaskExecutor")
    public AsyncTaskExecutor bioTaskExecutor() {
      return new ConcurrentTaskExecutor(virtualThreadExecutor());
    }

    @Bean(destroyMethod = "close")
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
