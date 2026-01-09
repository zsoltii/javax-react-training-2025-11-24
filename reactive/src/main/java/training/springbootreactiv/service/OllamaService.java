package training.springbootreactiv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.OllamaMessageRequestDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class OllamaService {

    private final ChatClient chatClient;

    public Flux<ChatResponse> chat(Mono<OllamaMessageRequestDto> request) {
        return request.map(OllamaMessageRequestDto::question)
                .flatMapMany(
                        question ->
                                chatClient
                                        .prompt()
                                        .user(question)
                                        // it doesn't work
                                        // with none thinking models
                                        // .options(
                                        //        OllamaChatOptions.builder()
                                        //                .enableThinking()
                                        //                .build())
                                        .stream()
                                        .chatResponse());
    }
}
