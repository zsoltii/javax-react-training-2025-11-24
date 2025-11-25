package training.springbootreactiv.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.EmployeeDto;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    @Query("SELECT id, name, year_of_birth FROM employee")
    Flux<EmployeeDto> findDtoAll();

    Mono<EmployeeDto> findDtoById(Long id);
}
