package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.batch.run.incrementer.CustomJobParametersIncementer;
import spring.batch.run.vlidator.CustomJobParametersValidator;

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
                .next(step3())
                //.validator(new CustomJobParametersValidator())
                //.validator(new DefaultJobParametersValidator(new String[]{"name","date"},new String[]{"count"}))
                //.incrementer(new CustomJobParametersIncementer())
                .incrementer(new RunIdIncrementer())

                //Job이 수행되기전에 JobRepository의 기능이 시작되기 전 단계
                //Job이 수행되기 직전에
                //총 2번의 검증단계를 거침
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
                       // chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED);
                        //stepContribution.setExitStatus(ExitStatus.STOPPED);
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
