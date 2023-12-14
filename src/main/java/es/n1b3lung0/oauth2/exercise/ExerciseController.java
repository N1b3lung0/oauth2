package es.n1b3lung0.oauth2.exercise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;

    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Exercise> findById(@PathVariable Long requestedId) {
        return exerciseRepository.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise newExercise, UriComponentsBuilder uriComponentsBuilder) {
        Exercise savedExercise = this.exerciseRepository.save(newExercise);
        URI locationOfNewExercise = uriComponentsBuilder
                .path("exercises/{id}")
                .buildAndExpand(savedExercise.id())
                .toUri();
        return ResponseEntity.created(locationOfNewExercise).body(savedExercise);
    }
}
