package es.n1b3lung0.oauth2.exercise;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;

    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @PostAuthorize("returnObject.body != null ? returnObject.body.owner == authentication.name : true")
    @GetMapping("/{requestedId}")
    public ResponseEntity<Exercise> findById(@PathVariable Long requestedId) {
        return this.exerciseRepository.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody ExerciseRequest exerciseRequest, UriComponentsBuilder uriComponentsBuilder, @CurrentOwner String owner) {
        Exercise exercise = new Exercise(exerciseRequest.name(), owner);
        Exercise savedExercise = this.exerciseRepository.save(exercise);
        URI locationOfNewExercise = uriComponentsBuilder
                .path("exercises/{id}")
                .buildAndExpand(savedExercise.id())
                .toUri();
        return ResponseEntity.created(locationOfNewExercise).body(savedExercise);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> findAll(Pageable pageable, @CurrentOwner String owner) {
        return ResponseEntity.ok(this.exerciseRepository.findByOwner(
                owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"))
                )
        ).getContent());
    }
}
