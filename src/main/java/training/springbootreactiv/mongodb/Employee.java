package training.springbootreactiv.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.relational.core.mapping.Column;

@Document(collection = "employees")
public record Employee(
        @Id String id, @Column("name") String name, @Column("year_of_birth") Integer yearOfBirth) {}
