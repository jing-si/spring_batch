package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job BatchJob(){
        return jobBuilderFactory.get("BatchJob")
                .start(jobStep(null))
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    @Bean
    public Step jobStep(JobLauncher jobLauncher){
        //Bean이 생성되는 시점에 JobLauncher가 DI되어서
        //start jobStep(null)로 실행시켜도 문제가 없는 듯?
        return stepBuilderFactory.get("jobStep")
                .job(childJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().put("jobStep","put jobStep OK!");
                        stepExecution.getExecutionContext().put("name","put name OK!");
                        stepExecution.getExecutionContext().putLong("count",100L);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return null;
                    }
                })
                .build();

    }

    private DefaultJobParametersExtractor jobParametersExtractor(){

        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        //ExecutionContext 내의 key,value를 가지고 올 수 있는 클래스
        //JobParameters 내에서도 가지고 올 수 있다.
        //그럼 둘다 가지고 있음 뭘가져올까?
        //ExecutionContext 값이 최종 값이 되는 듯 하다.
        extractor.setKeys(new String[]{"jobStep","name","count(long)"});
        return extractor;
    }

    private Job childJob() {
        return jobBuilderFactory.get("childJob")
                .start(step1())
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
                        JobParameters jobParameters = stepContribution.getStepExecution().getJobParameters();
                        String name = jobParameters.getString("name");
                        String jobStep = jobParameters.getString("jobStep");
                        Long count = jobParameters.getLong("count");
                        System.out.println("=======================");
                        System.out.println(">>step1  Batch!!");
                        System.out.println(">>name = "+name);
                        System.out.println(">>jobStep = "+jobStep);
                        System.out.println(">>count = "+count);
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
