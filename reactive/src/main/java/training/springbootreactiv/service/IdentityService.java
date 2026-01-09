package training.springbootreactiv.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.db.Identity;
import training.springbootreactiv.db.IdentityRepository;
import training.springbootreactiv.dto.IdentityDto;

@RequiredArgsConstructor
@Service
public class IdentityService {
    private final IdentityRepository repository;

    public Flux<IdentityDto> findAll() {
        return repository.findDtoAll();
    }

    public Mono<IdentityDto> findById(UUID id) {
        return repository.findDtoById(id, IdentityDto.class);
    }

    @Transactional
    public Mono<IdentityDto> save(Mono<IdentityDto> identityDto) {
        return identityDto
                //                .log()
                .map(IdentityService::toIdentity)
                //                .log()
                .flatMap(repository::save)
                //                .log()
                .map(IdentityService::identityDto);
        //                .log();
    }

    @Transactional
    public Mono<Void> delete(Mono<IdentityDto> identityDto) {
        return identityDto.map(IdentityService::toIdentity).flatMap(repository::delete);
    }

    private static Identity toIdentity(IdentityDto identityDto) {
        return new Identity(identityDto.id());
    }

    private static IdentityDto identityDto(Identity identity) {
        return new IdentityDto(identity.id());
    }
}
