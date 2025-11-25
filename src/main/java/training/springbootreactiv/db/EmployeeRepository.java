package training.springbootreactiv.db;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EmployeeRepository {

    private final AtomicLong sequenceGenerator = new AtomicLong(4);

    private final List<Employee> employees = new CopyOnWriteArrayList<>(List.of(
            new Employee(1L, "John Doe", 1990),
            new Employee(2L, "Jane Smith", 1985),
            new Employee(3L, "Alice Johnson", 1992),
            new Employee(4L, "Bob Brown", 1988)
    ));

    public Flux<Employee> findAll() {
        return Flux.fromIterable(employees);
    }

    public Mono<Employee> findById(Long id) {
        return Flux.fromIterable(employees)
                .filter(e -> e.getId().equals(id))
                .single();
    }

    public Mono<Employee> save(Employee employee) {
        if(employee.getId() == null) {
            Long id = sequenceGenerator.incrementAndGet();
            Employee newEmployee = new Employee(id, employee.getName(), employee.getYearOfBirth());
            employees.add(newEmployee);
            return Mono.just(newEmployee);
        } else {
            // java 8 stream - start
//            return findById(employee.getId())
//                    .peak(e -> {
//                        // sideEffect, dont do that!!!!!
//                        e.setName(employee.getName());
//                        e.setYearOfBirth(employee.getYearOfBirth());
//                    })
//                    .map(e -> new Employee(e.getId(), e.getName(), e.getYearOfBirth()));
//
//            return findById(employee.getId())
//                    .peak(e -> { // JVM remove this peak from the code, because of optimization
//                        // sideEffect, don't do that!!!!!
//                        e.setName(employee.getName());
//                        e.setYearOfBirth(employee.getYearOfBirth());
//                    })
//                    .count();
            // java 8 stream - end


            return findById(employee.getId())
                    .doOnNext(e -> { // java 8 stream -> peak()
                        // sideEffect, dont do that!!!!!
                        e.setName(employee.getName());
                        e.setYearOfBirth(employee.getYearOfBirth());
                    })
                    .map(e -> new Employee(e.getId(), e.getName(), e.getYearOfBirth()));
        }
    }

    public Mono<Void> deleteById(Long id) {
//        boolean success = employees.removeIf(e -> e.getId().equals(id));
//        if (success) {
//            return Mono.empty();
//        } else {
//            return Mono.error(new IllegalArgumentException("Employee not found: %d".formatted(id)));
//        }

        return findById(id)
                .doOnNext(employees::remove)
                .then();
    }
}
