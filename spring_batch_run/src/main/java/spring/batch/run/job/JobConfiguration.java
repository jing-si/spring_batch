package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job BatchJob(){
        return jobBuilderFactory.get("BatchJob")
                .incrementer(new RunIdIncrementer())
                .start(flow1())
                .split(taskExecutor()).add(flow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Flow flow1() {
        TaskletStep step = stepBuilderFactory.get("step in the flow1")
                .tasklet(tasklet()).build();

        return new FlowBuilder<Flow>("flow1")
                .start(step)
                .build();
    }

    @Bean
    public Flow flow2() {
        TaskletStep step2 = stepBuilderFactory.get("step2 in the flow1")
                .tasklet(tasklet()).build();
        TaskletStep step3 = stepBuilderFactory.get("step3 in the flow1")
                .tasklet(tasklet()).build();

        return new FlowBuilder<Flow>("flow1")
                .start(step2)
                .next(step3)
                .build();
    }


    @Bean
    public Tasklet tasklet() {
        return new CustomTasklet();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("async-thread-");
        return taskExecutor;
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



}
