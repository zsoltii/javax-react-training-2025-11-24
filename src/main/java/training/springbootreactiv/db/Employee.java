package training.springbootreactiv.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee {
    private Long id;
    private String name;
    private Integer yearOfBirth;
}
