package training.springbootreactiv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.OllamaMessageRequestDto;
import training.springbootreactiv.dto.OllamaMessageResponseDto;
import training.springbootreactiv.service.OllamaService;

@RestController
@RequestMapping("/api/ollama/chat")
@RequiredArgsConstructor
public class OllamaController {

    private final OllamaService ollamaService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<OllamaMessageResponseDto> chat(@RequestBody Mono<OllamaMessageRequestDto> request) {
        return ollamaService
                .chat(
                        request.filter(req -> StringUtils.hasText(req.question()))
                                .switchIfEmpty(
                                        Mono.error(
                                                new IllegalArgumentException(
                                                        "Question field is blank in request object"))))
                .map(
                        response ->
                                new OllamaMessageResponseDto(
                                        response.getResult().getOutput().getText(),
                                        // documentation:
                                        // https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html#_enabling_thinking_mode
                                        // it doesn't work with ollama: 2025.12.24
                                        response.getMetadata().get("thinking")));
    }
}
