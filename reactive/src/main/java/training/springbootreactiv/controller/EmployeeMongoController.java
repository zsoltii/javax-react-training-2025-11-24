package training.springbootreactiv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.EmployeeMongoDto;
import training.springbootreactiv.dto.EmployeeNameMongoDto;
import training.springbootreactiv.service.EmployeeMongoService;

@RestController
@RequestMapping("/api/mongo/employees")
@RequiredArgsConstructor
public class EmployeeMongoController {

    private final EmployeeMongoService employeeService;

    @GetMapping
    public Flux<EmployeeMongoDto> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeMongoDto>> findById(@PathVariable("id") String id) {
        return employeeService
                .findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/name")
    public Mono<ResponseEntity<EmployeeNameMongoDto>> findNameById(@PathVariable("id") String id) {
        return employeeService
                .findNameById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<EmployeeMongoDto>> save(
            @RequestBody Mono<EmployeeMongoDto> employeeDto, UriComponentsBuilder uriBuilder) {
        return employeeService
                .save(employeeDto)
                .map(
                        e ->
                                ResponseEntity.created(
                                                uriBuilder
                                                        .path("/api/employees/{id}") // it is
                                                        // possible
                                                        // from
                                                        // method
                                                        .buildAndExpand(e.id())
                                                        .toUri())
                                        .build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<EmployeeMongoDto>> update(
            @PathVariable("id") String id, @RequestBody Mono<EmployeeMongoDto> employeeDto) {
        return employeeDto
                .filter(e -> e.id() != null && e.id().equals(id))
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Employee ID in path and body must match: %s"
                                                .formatted(id))))
                .flatMap(e -> employeeService.save(Mono.just(e)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id) {
        return employeeService.deleteById(id);
    }
}
