package es.n1b3lung0.oauth2.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "Carlos", authorities = {"SCOPE_exercise:read"})
class ExerciseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldReturnAnExerciseWhenDataIsSaved() throws Exception {
        this.mvc.perform(get("/exercises/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.owner").value("Carlos"));
    }

    @WithMockUser(username = "Andrés", authorities = {"SCOPE_exercise:read", "SCOPE_exercise:write"})
    @Test
    @DirtiesContext
    void shouldCreateANewExercise() throws Exception {
        String location = this.mvc.perform(post("/exercises")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Exercise1"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        assert location != null;

        this.mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercise1"))
                .andExpect(jsonPath("$.owner").value("Andrés"));
    }

    @Test
    void shouldReturnAllExercisesWhenListIsRequested() throws Exception {
        this.mvc.perform(get("/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$..owner").value(everyItem(equalTo("Carlos"))));
    }

    @WithMockUser(username = "Andrés", authorities = {"SCOPE_exercise:read"})
    @Test
    void shouldReturnForbiddenWhenCardBelongsToSomeoneElse() throws Exception {
        this.mvc.perform(get("/exercises/99"))
                .andExpect(status().isForbidden());
    }
}