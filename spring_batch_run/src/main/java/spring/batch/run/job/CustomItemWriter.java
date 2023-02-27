package spring.batch.run.job;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CustomItemWriter implements ItemStreamWriter<String> {


    @Override
    public void write(List<? extends String> items) throws Exception {
        items.forEach(item -> System.out.println(item));
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("writer open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("writer update");
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("writer close");
    }


}
