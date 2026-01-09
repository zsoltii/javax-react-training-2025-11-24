package training.springbootblocking.controller;

import java.util.List;
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
import training.springbootblocking.dto.IdentityDto;
import training.springbootblocking.service.IdentityService;

@RestController
@RequestMapping("/api/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping
    public List<IdentityDto> findAll() {
        return identityService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityDto> findById(@PathVariable UUID id) {
        return identityService
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IdentityDto> save(
            @RequestBody IdentityDto identityDto, UriComponentsBuilder uriBuilder) {
        IdentityDto saved = identityService.save(identityDto);
        return ResponseEntity.created(
                        uriBuilder.path("/api/identity/{id}").buildAndExpand(saved.id()).toUri())
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody IdentityDto identityDto) {
        identityService.delete(identityDto);
        return ResponseEntity.noContent().build();
    }
}
