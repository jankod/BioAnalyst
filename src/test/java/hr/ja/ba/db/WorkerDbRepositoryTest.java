package hr.ja.ba.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database=default",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:bioanalyst-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class WorkerDbRepositoryTest {

    @Autowired
    private WorkerDbRepository repository;

    @Test
    void savePersistsWorkerNameAndStatus() {
        WorkerDb worker = new WorkerDb();
        worker.setName("worker-01");
        worker.setStatus(WorkerStatusDb.RUNNING);

        WorkerDb saved = repository.save(worker);

        Optional<WorkerDb> reloaded = repository.findById(saved.getId());
        assertThat(reloaded).isPresent();
        WorkerDb found = reloaded.orElseThrow();
        assertThat(found.getName()).isEqualTo("worker-01");
        assertThat(found.getStatus()).isEqualTo(WorkerStatusDb.RUNNING);
    }

    @Test
    void findAllReturnsEveryPersistedWorker() {
        WorkerDb pending = new WorkerDb();
        pending.setName("pending-worker");
        pending.setStatus(WorkerStatusDb.PENDING);

        WorkerDb completed = new WorkerDb();
        completed.setName("completed-worker");
        completed.setStatus(WorkerStatusDb.COMPLETED);

        repository.saveAll(List.of(pending, completed));

        List<WorkerDb> workers = repository.findAll();

        assertThat(workers).hasSize(2);
        assertThat(workers)
                .extracting(WorkerDb::getName, WorkerDb::getStatus)
                .containsExactlyInAnyOrder(
                        tuple("pending-worker", WorkerStatusDb.PENDING),
                        tuple("completed-worker", WorkerStatusDb.COMPLETED)
                );
    }
}
