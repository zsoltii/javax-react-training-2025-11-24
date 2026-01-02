package training.springbootreactiv.db;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import training.springbootreactiv.dto.IdentityDto;

public interface IdentityRepository extends ReactiveCrudRepository<Identity, UUID> {

    @Query("SELECT id FROM identity")
    Flux<IdentityDto> findDtoAll();

    <T> Mono<T> findDtoById(UUID id, Class<T> clazz);
}
