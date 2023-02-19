package spring.batch.run.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job BatchJob() throws InterruptedException {
        return jobBuilderFactory.get("BatchJob")
                .incrementer(new RunIdIncrementer())
                //.start(step1())
                .start(step2())
                .listener(new StopWatchJobListener())
                .build();
    }


    @Bean
    public Step step1() throws InterruptedException {
        return stepBuilderFactory.get("step1")
                .<Customer,Customer>chunk(100)
                .reader(pagingItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .build();

    }
    @Bean
    public Step step2() throws InterruptedException {
        return stepBuilderFactory.get("asyncStep1")
                .<Customer,Customer>chunk(100)
                .reader(pagingItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();

    }


    @Bean
    public ItemProcessor<Customer,Customer> customItemProcessor() throws InterruptedException {


        return new ItemProcessor<Customer, Customer>() {
            @Override
            public Customer process(Customer item) throws Exception {
                Thread.sleep(30);
                System.out.println("process!!");
                return new Customer(item.getId(), item.getFirstname().toUpperCase()
                ,item.getLastname().toUpperCase(),item.getBirthdate());
            }

        };
    }

    private JdbcBatchItemWriter customItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("insert into customer2 values(:id, :firstname, :lastname, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;

    }


    @Bean
    public AsyncItemWriter asyncItemWriter() {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        //위임할 클래스 지정 필요
        asyncItemWriter.setDelegate(customItemWriter());
        return asyncItemWriter;
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws InterruptedException {
        AsyncItemProcessor<Customer,Customer> anyAsyncItemProcessor = new AsyncItemProcessor<>();
        //AsyncItemProcessor는 Delegate로 실행할 Processor를 지정해줘야한다.
        anyAsyncItemProcessor.setDelegate(customItemProcessor());
        //TaskExecutor 설정 필요
        anyAsyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        //Bean으로 만들지 않으면 해당 명령도 필요함
        //anyAsyncItemProcessor.afterPropertiesSet();
        return anyAsyncItemProcessor;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader(){
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<Customer>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(300);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id",Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

}
