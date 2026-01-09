package training.springbootblocking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import training.springbootblocking.dto.IdentityDto;
import training.springbootblocking.entity.Identity;
import training.springbootblocking.repository.IdentityRepository;

@RequiredArgsConstructor
@Service
public class IdentityService {
    private final IdentityRepository repository;

    public List<IdentityDto> findAll() {
        return repository.findAll().stream().map(IdentityService::toIdentityDto).toList();
    }

    public Optional<IdentityDto> findById(UUID id) {
        return repository.findById(id).map(IdentityService::toIdentityDto);
    }

    @Transactional
    public IdentityDto save(IdentityDto identityDto) {
        Identity identity = toIdentity(identityDto);
        Identity saved = repository.save(identity);
        return toIdentityDto(saved);
    }

    @Transactional
    public void delete(IdentityDto identityDto) {
        Identity identity = toIdentity(identityDto);
        repository.delete(identity);
    }

    private static Identity toIdentity(IdentityDto identityDto) {
        return new Identity(identityDto.id());
    }

    private static IdentityDto toIdentityDto(Identity identity) {
        return new IdentityDto(identity.getId());
    }
}
