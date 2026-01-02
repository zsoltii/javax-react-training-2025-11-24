package training.springbootreactiv.db;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("identity")
public record Identity(@Id UUID id) implements Persistable<UUID> {
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true; // Mindig INSERT-et kényszerítünk
    }
}
