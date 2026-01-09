package training.springbootblocking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "identity")
@NoArgsConstructor
public class Identity {

    @Id @Getter @Setter private UUID id;

    public Identity(UUID id) {
        this.id = id;
    }
}
