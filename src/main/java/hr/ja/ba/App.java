package hr.ja.ba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication(scanBasePackages = "hr.ja.ba")
@EnableAsync
public class App {

    public static void main(String[] args) {
        log.info("Starting BioAnalyst shell");
        SpringApplication.run(App.class, args);
    }
}
