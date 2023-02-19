package spring.batch.run.job;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer,Customer> {


    @Override
    public Customer process(Customer item) throws Exception {
        System.out.println("process");
        System.out.print(item +"->");

        item.setName(item.getName().toUpperCase());
        System.out.println(item);
        return item;
    }

}
