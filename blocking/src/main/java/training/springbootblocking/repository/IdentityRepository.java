package training.springbootblocking.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import training.springbootblocking.entity.Identity;

public interface IdentityRepository extends JpaRepository<Identity, UUID> {}
