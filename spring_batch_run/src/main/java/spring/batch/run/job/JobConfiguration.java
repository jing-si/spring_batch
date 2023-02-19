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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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
    public Job BatchJob() throws Exception{
        return jobBuilderFactory.get("BatchJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() throws Exception{
        return stepBuilderFactory.get("step1")
                .<Customer,Customer>chunk(10)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> customerItemReader() throws Exception{
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("firstname","%1");
        return  new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPaginReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .queryProvider(createQueryProvider())
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id,firstname,lastname,birthdate");
        factoryBean.setFromClause("from customer");
        factoryBean.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id",Order.ASCENDING);

        factoryBean.setSortKeys(sortKeys);

        return factoryBean.getObject();
    }

    @Bean
    public ItemWriter<Customer> customerItemWriter(){
        return items -> {
            for(Customer item : items){
                System.out.println(item.toString());
            }
        };
    }

}
