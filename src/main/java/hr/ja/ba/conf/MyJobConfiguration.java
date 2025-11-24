package hr.ja.ba.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@EnableBatchProcessing
public class MyJobConfiguration {

    /**
     * Note the JobRepository is typically autowired in and not needed to be explicitly
     * configured
     */
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
        return new JobBuilder("sampleJob", jobRepository)
              .start(sampleStep)
              .build();
    }

    /**
     * Note the TransactionManager is typically autowired in and not needed to be explicitly
     * configured
     */
    @Bean
    public Step sampleStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
              .<String, String>chunk(10, transactionManager)
              .reader(itemReader())
              .writer(itemWriter())
              .build();
    }

    private ItemWriter<? super String> itemWriter() {
        return null;
    }

    private ItemReader<String> itemReader() {
        return null;
    }

}
