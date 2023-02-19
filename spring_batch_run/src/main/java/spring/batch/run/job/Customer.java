package spring.batch.run.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Customer {
    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;


}
