package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job BatchJob(){
        return jobBuilderFactory.get("BatchJob")
                .start(step1())
                .next(step2())
                .next(chunkStep())
                .build();
    }
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {


                    System.out.println("=======================");
                    System.out.println(">>step2  Batch!!");
                    System.out.println("=======================");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("=======================");
                        System.out.println(">>step1  Batch!!");
                        System.out.println("=======================");
                        return RepeatStatus.FINISHED;
                    }
                }).build();

    }
    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("=======================");
                        System.out.println(">>step3  Batch!!");
                        System.out.println("=======================");
                        return RepeatStatus.FINISHED;
                    }
                }).build();

    }

    @Bean
    public Step chunkStep(){
        return stepBuilderFactory.get("chunkStep")
                .<String,String>chunk(10)
                .reader(new ListItemReader<>(Arrays.asList("item1","item2","item3","item4")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return item.toUpperCase();
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> items) throws Exception {
                        items.forEach(item -> System.out.println(item));
                    }
                })
                .build();
    }



}
