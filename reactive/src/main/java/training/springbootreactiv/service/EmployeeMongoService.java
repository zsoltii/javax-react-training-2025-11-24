package training.springbootreactiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.EmployeeMongoDto;
import training.springbootreactiv.dto.EmployeeNameMongoDto;
import training.springbootreactiv.mongodb.Employee;
import training.springbootreactiv.mongodb.EmployeeMongoRepository;

@RequiredArgsConstructor
@Service
public class EmployeeMongoService {

    private final EmployeeMongoRepository repository;

    public Flux<EmployeeMongoDto> findAll() {
        return repository.findAllBy();
    }

    public Mono<EmployeeMongoDto> findById(String id) {
        return repository.findDtoById(id, EmployeeMongoDto.class);
    }

    public Mono<EmployeeNameMongoDto> findNameById(String id) {
        return repository.findDtoById(id, EmployeeNameMongoDto.class);
    }

    @Transactional
    public Mono<EmployeeMongoDto> save(Mono<EmployeeMongoDto> EmployeeMongoDto) {
        return EmployeeMongoDto.map(EmployeeMongoService::toEmployee)
                .flatMap(repository::save)
                .map(EmployeeMongoService::toEmployeeMongoDto);
    }

    @Transactional
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    private static EmployeeMongoDto toEmployeeMongoDto(Employee e) {
        return new EmployeeMongoDto(e.id(), e.name(), e.yearOfBirth());
    }

    private static Employee toEmployee(EmployeeMongoDto dto) {
        return new Employee(dto.id(), dto.name(), dto.yearOfBirth());
    }
}
