package training.javaxreact.emplees;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EmployeeService {

    private List<Empleyee> employees =
            List.of(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985));

    public Mono<Empleyee> findEmployeeByName(String name) {
        return Flux.fromIterable(employees)
                .filter(e -> e.name().equalsIgnoreCase(name))
                .singleOrEmpty();
    }
}
