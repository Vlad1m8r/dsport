package ru.weu.dsport.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.service.ExerciseCatalogService;

@WebMvcTest(controllers = ExerciseCatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExerciseCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseCatalogService exerciseCatalogService;

    @Test
    void listExercisesUsesDefaultScopeWhenParameterMissing() throws Exception {
        when(exerciseCatalogService.listExercises(ExerciseScope.ALL, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk());

        verify(exerciseCatalogService).listExercises(ExerciseScope.ALL, null, null);
    }

    @Test
    void listExercisesAcceptsAllScope() throws Exception {
        when(exerciseCatalogService.listExercises(ExerciseScope.ALL, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/exercises").param("scope", "ALL"))
                .andExpect(status().isOk());

        verify(exerciseCatalogService).listExercises(ExerciseScope.ALL, null, null);
    }

    @Test
    void listExercisesAcceptsSystemScope() throws Exception {
        when(exerciseCatalogService.listExercises(ExerciseScope.SYSTEM, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/exercises").param("scope", "SYSTEM"))
                .andExpect(status().isOk());

        verify(exerciseCatalogService).listExercises(ExerciseScope.SYSTEM, null, null);
    }

    @Test
    void listExercisesAcceptsMyScope() throws Exception {
        when(exerciseCatalogService.listExercises(ExerciseScope.MY, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/exercises").param("scope", "MY"))
                .andExpect(status().isOk());

        verify(exerciseCatalogService).listExercises(ExerciseScope.MY, null, null);
    }
}
