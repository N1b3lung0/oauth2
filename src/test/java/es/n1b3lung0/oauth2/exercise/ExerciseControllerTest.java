package es.n1b3lung0.oauth2.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
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

    @Test
    @DirtiesContext
    void shouldCreateANewExercise() throws Exception {
        String location = this.mvc.perform(post("/exercises")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Exercise1",
                                    "owner": "Carlos"
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
                .andExpect(jsonPath("$.owner").value("Carlos"));
    }

}