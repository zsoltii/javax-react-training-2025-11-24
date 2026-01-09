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
import training.springbootreactiv.dto.EmployeeDto;
import training.springbootreactiv.dto.EmployeeNameDto;
import training.springbootreactiv.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Flux<EmployeeDto> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> findById(@PathVariable("id") Long id) {
        return employeeService
                .findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/name")
    public Mono<ResponseEntity<EmployeeNameDto>> findNameById(@PathVariable("id") Long id) {
        return employeeService
                .findNameById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<EmployeeDto>> save(
            @RequestBody Mono<EmployeeDto> employeeDto, UriComponentsBuilder uriBuilder) {
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
    public Mono<ResponseEntity<EmployeeDto>> update(
            @PathVariable("id") Long id, @RequestBody Mono<EmployeeDto> employeeDto) {
        return employeeDto
                .filter(e -> e.id() != null && e.id().equals(id))
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Employee ID in path and body must match: %d"
                                                .formatted(id))))
                .flatMap(e -> employeeService.save(Mono.just(e)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable("id") Long id) {
        return employeeService.deleteById(id);
    }
}
