package spring.batch.run;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchRunApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchRunApplication.class, args);
    }

}
