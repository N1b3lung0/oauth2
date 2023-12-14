package es.n1b3lung0.oauth2.exercise;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "EXERCISES")
public record Exercise(@Id Long id, String name, String owner) {
    public Exercise(String name, String owner) {
        this(null, name, owner);
    }
}
