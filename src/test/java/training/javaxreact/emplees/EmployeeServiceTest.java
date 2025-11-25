package training.javaxreact.emplees;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class EmployeeServiceTest {

    @Test
    void findEmployeeName() {
        EmployeeService service = new EmployeeService();
        StepVerifier.create(service.findEmployeeByName("John Doe"))
                .expectNextMatches(e -> e.name().equals("John Doe"))
                .verifyComplete();

        StepVerifier.create(service.findEmployeeByName("John Doe"))
                .expectNext(new Empleyee("John Doe", 1990))
                .verifyComplete();
    }
}