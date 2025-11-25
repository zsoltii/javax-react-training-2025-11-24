package training.springbootreactiv.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("employee")
public class Employee {
    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("year_of_birth")
    private Integer yearOfBirth;
}
