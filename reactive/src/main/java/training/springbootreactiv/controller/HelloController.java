package training.springbootreactiv.controller;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.HelloMessageDto;
import training.springbootreactiv.gateway.HelloClient;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {

    private final HelloClient helloClient;

    @GetMapping
    public Mono<HelloMessageDto> hello() {
        return Mono.just(
                new HelloMessageDto(
                        "Hello, World! The time is: %s".formatted(LocalDateTime.now().toString())));
        //                .log();
    }

    @GetMapping("/webclient")
    public Mono<HelloMessageDto> helloWebCLient() {
        return helloClient
                .hello()
                //                .log()
                .map(hello -> new HelloMessageDto("From WebClient: %s".formatted(hello.message())));
    }
}
