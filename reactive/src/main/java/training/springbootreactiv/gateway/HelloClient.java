package training.springbootreactiv.gateway;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.HelloMessageDto;

@HttpExchange(url = "/hello")
public interface HelloClient {

    @GetExchange
    Mono<HelloMessageDto> hello();
}
