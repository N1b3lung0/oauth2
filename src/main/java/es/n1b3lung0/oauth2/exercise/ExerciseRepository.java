package es.n1b3lung0.oauth2.exercise;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExerciseRepository extends CrudRepository<Exercise, Long>, PagingAndSortingRepository<Exercise, Long> {

    Page<Exercise> findByOwner(String owner, PageRequest pageRequest);

    @Query("select * from exercises ex where ex.owner = :#{authentication.name}")
    Iterable<Exercise> findAll();
}
