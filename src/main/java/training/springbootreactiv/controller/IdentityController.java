package training.springbootreactiv.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.IdentityDto;
import training.springbootreactiv.service.IdentityService;

@RestController
@RequestMapping("/api/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping
    public Flux<IdentityDto> findAll() {
        return identityService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<IdentityDto>> findById(@PathVariable("id") UUID id) {
        return identityService
                .findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<IdentityDto>> save(
            @RequestBody Mono<IdentityDto> identityDto, UriComponentsBuilder uriBuilder) {
        return identityService
                .save(identityDto)
                .map(
                        e ->
                                ResponseEntity.created(
                                                uriBuilder
                                                        .path("/api/identity/{id}") // it is
                                                        // possible
                                                        // from
                                                        // method
                                                        .buildAndExpand(e.id())
                                                        .toUri())
                                        .build());
    }

    @DeleteMapping
    public Mono<Void> delete(@RequestBody Mono<IdentityDto> identityDto) {
        return identityService.delete(identityDto);
    }
}
