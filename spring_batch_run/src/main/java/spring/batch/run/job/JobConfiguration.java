package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.batch.api.listener.JobListener;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job BatchJob(){
        return jobBuilderFactory.get("BatchJob")
                .incrementer(new RunIdIncrementer())
                .start(step1(null))
                .next(step2())
                .next(step3())
                .listener(new CustJobListener())
                .build();
    }
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .listener(new CustStepListener())
                .tasklet(tasklet2(null)).build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters['message']}") String message){
        System.out.println(message);
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1(null)).build();

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
    @StepScope
    public Tasklet tasklet1(@Value("#{jobExecutionContext['name']}")String name){
        System.out.println("name = " + name);
        return ( stepContribution,  chunkContext) ->{
            System.out.println("tasklet1 has executed");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet tasklet2(@Value("#{jobExecutionContext['name2']}")String name2){
        System.out.println("name2 = " + name2);
        return ( stepContribution,  chunkContext) ->{
            System.out.println("tasklet2 has executed");
            return RepeatStatus.FINISHED;
        };
    }



}
