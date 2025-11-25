package training.springbootreactiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.db.Employee;
import training.springbootreactiv.db.EmployeeRepository;
import training.springbootreactiv.dto.EmployeeDto;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public Flux<EmployeeDto> findAll() {
        return repository.findAll().map(EmployeeService::toEmployeeDto);
    }

    public Mono<EmployeeDto> findById(Long id) {
        return repository.findById(id).map(EmployeeService::toEmployeeDto);
    }

    public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
        return employeeDto
                .map(EmployeeService::toEmployee)
                .flatMap(repository::save)
                .map(EmployeeService::toEmployeeDto);
    }

    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    private static EmployeeDto toEmployeeDto(Employee e) {
        return new EmployeeDto(e.id(), e.name(), e.yearOfBirth());
    }

    private static Employee toEmployee(EmployeeDto dto) {
        return new Employee(dto.id(), dto.name(), dto.yearOfBirth());
    }
}
