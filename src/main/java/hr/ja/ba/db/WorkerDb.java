package hr.ja.ba.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Table
@Entity
public class WorkerDb extends AbstractPersistable<Long> {

    private String name;

    private WorkerStatusDb status;

}
