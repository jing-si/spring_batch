package spring.batch.run.job;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CustomItemWriter implements ItemWriter<Customer> {
    @Override
    public void write(List<? extends Customer> items) throws Exception {
        System.out.println("write");
        System.out.println(items);
        items.forEach(item -> System.out.println(item));
    }
}
