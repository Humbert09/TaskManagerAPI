
package yaganaste.taskmanagerapi.controller;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import yaganaste.taskmanagerapi.dto.TaskCreateDTO;
import yaganaste.taskmanagerapi.dto.TaskUpdateDTO;
import yaganaste.taskmanagerapi.dto.TaskResponseDTO;
import yaganaste.taskmanagerapi.enums.TaskStatus;
import yaganaste.taskmanagerapi.service.TaskService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    // =============================================
    // TEST 1: POST - CREAR TAREA
    // =============================================

    @Test
    void createTask_shouldReturn201() throws Exception {

        TaskResponseDTO response =
                createResponse(1, TaskStatus.Pending);

        when(taskService.createTask(any(TaskCreateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/taskmanagement/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "title": "Estudiar Java",
                        "description": "Prueba de API"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        endsWith("/taskmanagement/task/1")
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("Pending"));

        verify(taskService).createTask(any(TaskCreateDTO.class));
    }

    // =============================================
    // TEST 2: GET - LISTAR TAREAS
    // =============================================

    @Test
    void getAllTasks_shouldReturn200() throws Exception {

        List<TaskResponseDTO> tasks = List.of(
                createResponse(1, TaskStatus.Pending),
                createResponse(2, TaskStatus.InProgress)
        );

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/taskmanagement/allTasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(taskService).getAllTasks();
    }

    // =============================================
    // TEST 3: GET - BUSCAR TAREA EXISTENTE
    // =============================================

    @Test
    void getTaskById_shouldReturn200() throws Exception {

        when(taskService.getTaskById(5))
                .thenReturn(createResponse(5, TaskStatus.Pending));

        mockMvc.perform(get("/taskmanagement/task/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Estudiar Java"));

        verify(taskService).getTaskById(5);
    }

    // =============================================
    // TEST 4: GET - TAREA INEXISTENTE
    // =============================================

    @Test
    void getTaskById_shouldReturn404() throws Exception {

        when(taskService.getTaskById(999))
                .thenThrow(new EntityNotFoundException(
                        "La tarea con ID 999 no fue encontrada."
                ));

        mockMvc.perform(get("/taskmanagement/task/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(
                        "La tarea con ID 999 no fue encontrada."
                ));
    }

    // =============================================
    // TEST 5: PUT - ACTUALIZAR TAREA
    // =============================================

    @Test
    void updateTaskById_shouldReturn200() throws Exception {

        when(taskService.updateTaskById(
                eq(3), any(TaskUpdateDTO.class)))
                .thenReturn(createResponse(
                        3, TaskStatus.InProgress
                ));

        mockMvc.perform(put("/taskmanagement/modifyTask/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "status": "InProgress"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("InProgress"));

        verify(taskService).updateTaskById(
                eq(3), any(TaskUpdateDTO.class));
    }

    // =============================================
    // TEST 6: PUT - TAREA COMPLETED
    // =============================================

    @Test
    void updateTaskById_shouldReturn409() throws Exception {

        when(taskService.updateTaskById(
                eq(6), any(TaskUpdateDTO.class)))
                .thenThrow(new IllegalStateException(
                        "No puedes modificar una tarea marcada como 'Completed'"
                ));

        mockMvc.perform(put("/taskmanagement/modifyTask/{id}", 6)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "description": "Intento de modificación"
                    }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "No puedes modificar una tarea marcada como 'Completed'"
                ));
    }

    // =============================================
    // TEST 7: DELETE - ELIMINAR TAREA
    // =============================================

    @Test
    void deleteTaskById_shouldReturn200() throws Exception {

        mockMvc.perform(
                        delete("/taskmanagement/deleteTask/{id}", 4)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "La tarea con Id: 4 fue eliminada con éxito"
                ));

        verify(taskService).deleteTasksById(4);
    }

    // =============================================
    // MÉTODO AUXILIAR
    // =============================================

    private TaskResponseDTO createResponse(
            Integer id, TaskStatus status) {

        return new TaskResponseDTO(
                id,
                "Estudiar Java",
                "Prueba de API",
                status,
                OffsetDateTime.parse("2026-10-16T08:00:00Z")
        );
    }
}
