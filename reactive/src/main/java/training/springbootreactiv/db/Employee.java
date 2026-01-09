package training.springbootreactiv.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("employee")
public record Employee(
        @Id Long id, @Column("name") String name, @Column("year_of_birth") Integer yearOfBirth) {}
