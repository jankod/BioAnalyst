package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import java.util.Arrays;

@Slf4j
// java -jar bioanalyst.jar --spring.config.additional-location=/etc/bioanalyst/
@SpringBootApplication
@RequiredArgsConstructor
public class App implements CommandLineRunner, ExitCodeGenerator {

    private final CommandLine.IFactory factory;
    private final BioanalystRootCommand rootCommand;
    private int exitCode;


    @Override
    public void run(String... args) {
        // let picocli parse command line args and run the business logic
        exitCode = new CommandLine(rootCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    static void main(String[] args) {
        log.info(Arrays.toString(args));
        System.exit(SpringApplication.exit(SpringApplication.run(App.class, args)));
    }
}
