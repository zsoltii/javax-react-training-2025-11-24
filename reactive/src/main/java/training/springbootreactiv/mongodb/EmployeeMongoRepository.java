package training.springbootreactiv.mongodb;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.EmployeeMongoDto;

public interface EmployeeMongoRepository extends ReactiveMongoRepository<Employee, String> {

    Flux<EmployeeMongoDto> findAllBy();

    <T> Mono<T> findDtoById(String id, Class<T> clazz);

    @Query(
            """
            { 'name' :  ?0 }
            """)
    Flux<EmployeeMongoDto> findByName(String name);

    @Query(
            """
            { 'name' :  ?0, 'year_of_birth' : {$gte: ?#{#yearOfBirth}} }
            """)
    Flux<EmployeeMongoDto> findByNameAndBirthYear(String name, Integer yearOfBirth);
}
