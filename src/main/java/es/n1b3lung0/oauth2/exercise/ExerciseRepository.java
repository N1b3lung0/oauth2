package es.n1b3lung0.oauth2.exercise;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ExerciseRepository extends CrudRepository<Exercise, Long> {
    Iterable<Exercise> findByOwner(String owner);

    @Query("select * from exercises ex where ex.owner = :#{authentication.name}")
    Iterable<Exercise> findAll();
}
