package training.springbootreactiv.config;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import training.springbootreactiv.dto.EmployeeDto;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class EmployeeKafkaGatewayConfig {
    @Bean
    public Consumer<Flux<EmployeeDto>> employeeEventsHandler() {
        return employee -> employee.subscribe(e -> log.info("Received events: {}", e));
    }
}
