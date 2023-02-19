package spring.batch.run.job;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;
}
