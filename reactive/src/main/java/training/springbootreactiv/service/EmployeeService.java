package training.springbootreactiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.db.Employee;
import training.springbootreactiv.db.EmployeeRepository;
import training.springbootreactiv.dto.EmployeeDto;
import training.springbootreactiv.dto.EmployeeNameDto;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final StreamBridge streamBridge;

    public Flux<EmployeeDto> findAll() {
        return repository.findDtoAll().flatMap(this::cacheInRedis);
    }

    public Mono<EmployeeDto> findById(Long id) {
        return reactiveRedisTemplate
                .opsForValue()
                .get(id)
                //                .log()
                .switchIfEmpty(
                        repository.findDtoById(id, EmployeeDto.class).flatMap(this::cacheInRedis));
        //                .log();
    }

    public Mono<EmployeeNameDto> findNameById(Long id) {
        return repository.findDtoById(id, EmployeeNameDto.class);
    }

    @Transactional
    public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
        return employeeDto
                .map(EmployeeService::toEmployee)
                .flatMap(repository::save)
                .map(EmployeeService::toEmployeeDto)
                .doOnNext(this::sendEventKafka)
                .flatMap(this::cacheInRedis);
    }

    @Transactional
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id).then(Mono.defer(() -> clearFromRedisCache(id)));
    }

    private boolean sendEventKafka(EmployeeDto employee) {
        return streamBridge.send("employeesEvents", employee);
    }

    private Mono<Void> clearFromRedisCache(Long id) {
        return reactiveRedisTemplate.opsForValue().delete(id).then();
    }

    private Mono<EmployeeDto> cacheInRedis(EmployeeDto e) {
        return reactiveRedisTemplate.opsForValue().set(e.id(), e).thenReturn(e);
    }

    private static EmployeeDto toEmployeeDto(Employee e) {
        return new EmployeeDto(e.id(), e.name(), e.yearOfBirth());
    }

    private static Employee toEmployee(EmployeeDto dto) {
        return new Employee(dto.id(), dto.name(), dto.yearOfBirth());
    }
}
