
package yaganaste.taskmanagerapi.service;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import yaganaste.taskmanagerapi.dto.TaskCreateDTO;
import yaganaste.taskmanagerapi.dto.TaskUpdateDTO;
import yaganaste.taskmanagerapi.dto.TaskResponseDTO;
import yaganaste.taskmanagerapi.enums.TaskStatus;
import yaganaste.taskmanagerapi.models.Tasks;
import yaganaste.taskmanagerapi.repository.TaskRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    // ==================================================
    // TEST 1: CREAR UNA TAREA
    // ==================================================

    @Test
    void createTask_shouldCreateTaskAsPending() {

        // Arrange
        TaskCreateDTO dto = new TaskCreateDTO(
                "Estudiar Java",
                "Repasar Spring Boot"
        );

        when(taskRepository.save(any(Tasks.class)))
                .thenAnswer(invocation -> {
                    Tasks task = invocation.getArgument(0);
                    ReflectionTestUtils.setField(task, "id", 1);
                    return task;
                });

        // Act
        TaskResponseDTO response = taskService.createTask(dto);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Estudiar Java", response.getTitle());
        assertEquals("Repasar Spring Boot", response.getDescription());
        assertEquals(TaskStatus.Pending, response.getStatus());
        assertNotNull(response.getCreationDate());

        verify(taskRepository).save(any(Tasks.class));
    }

    // ==================================================
    // TEST 2: OBTENER TODAS LAS TAREAS
    // ==================================================

    @Test
    void getAllTasks_shouldReturnTaskList() {

        // Arrange
        Tasks task1 = createMockTask(
                1, "Tarea 1", "Descripción 1", TaskStatus.Pending
        );

        Tasks task2 = createMockTask(
                2, "Tarea 2", "Descripción 2", TaskStatus.InProgress
        );

        when(taskRepository.findAll())
                .thenReturn(List.of(task1, task2));

        // Act
        List<TaskResponseDTO> response = taskService.getAllTasks();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Tarea 1", response.get(0).getTitle());
        assertEquals("Tarea 2", response.get(1).getTitle());

        verify(taskRepository).findAll();
    }

    // ==================================================
    // TEST 3: OBTENER TAREA EXISTENTE POR ID
    // ==================================================

    @Test
    void getTaskById_shouldReturnTaskWhenExists() {

        // Arrange
        Integer id = 5;

        Tasks task = createMockTask(
                id, "Estudiar SQL", "Repasar JOIN", TaskStatus.Pending
        );

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        // Act
        TaskResponseDTO response = taskService.getTaskById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Estudiar SQL", response.getTitle());
        assertEquals(TaskStatus.Pending, response.getStatus());

        verify(taskRepository).findById(id);
    }

    // ==================================================
    // TEST 4: OBTENER TAREA INEXISTENTE
    // ==================================================

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskDoesNotExist() {

        // Arrange
        Integer id = 999;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> taskService.getTaskById(id)
        );

        assertTrue(exception.getMessage().contains("999"));

        verify(taskRepository).findById(id);
    }

    // ==================================================
    // TEST 5: OBTENER TAREA CON ID INVÁLIDO
    // ==================================================

    @Test
    void getTaskById_shouldThrowExceptionForInvalidId() {

        // Arrange
        Integer id = 0;

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.getTaskById(id)
        );

        verifyNoInteractions(taskRepository);
    }

    // ==================================================
    // TEST 6: ACTUALIZAR DESCRIPCIÓN
    // ==================================================

    @Test
    void updateTaskById_shouldUpdateDescription() {

        // Arrange
        Integer id = 3;

        Tasks task = createMockTask(
                id, "Tarea original", "Descripción anterior",
                TaskStatus.Pending
        );

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setDescription("Descripción actualizada");

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        // Act
        TaskResponseDTO response = taskService.updateTaskById(id, dto);

        // Assert
        assertEquals(
                "Descripción actualizada",
                response.getDescription()
        );

        assertEquals(TaskStatus.Pending, response.getStatus());

        verify(taskRepository).save(task);
    }

    // ==================================================
    // TEST 7: ACTUALIZAR ESTADO
    // ==================================================

    @Test
    void updateTaskById_shouldUpdateStatus() {

        // Arrange
        Integer id = 4;

        Tasks task = createMockTask(
                id, "Tarea pendiente", "Sin completar",
                TaskStatus.Pending
        );

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setStatus(TaskStatus.InProgress);

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        // Act
        TaskResponseDTO response = taskService.updateTaskById(id, dto);

        // Assert
        assertEquals(
                TaskStatus.InProgress,
                response.getStatus()
        );

        assertEquals("Sin completar", response.getDescription());

        verify(taskRepository).save(task);
    }

    // ==================================================
    // TEST 8: IMPEDIR MODIFICAR TAREA COMPLETED
    // ==================================================

    @Test
    void updateTaskById_shouldRejectCompletedTask() {

        // Arrange
        Integer id = 6;

        Tasks task = createMockTask(
                id, "Tarea completada", "Trabajo terminado",
                TaskStatus.Completed
        );

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setDescription("Intento de modificación");

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        // Act + Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.updateTaskById(id, dto)
        );

        assertTrue(exception.getMessage().contains("Completed"));

        verify(taskRepository, never()).save(any(Tasks.class));
    }

    // ==================================================
    // TEST 9: ELIMINAR TAREA PENDING
    // ==================================================

    @Test
    void deleteTasksById_shouldDeletePendingTask() {

        // Arrange
        Integer id = 7;

        Tasks task = createMockTask(
                id, "Tarea pendiente", "Eliminar esta tarea",
                TaskStatus.Pending
        );

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        // Act
        taskService.deleteTasksById(id);

        // Assert
        verify(taskRepository).delete(task);
    }

    // ==================================================
    // TEST 10: ELIMINAR TAREA IN PROGRESS
    // ==================================================

    @Test
    void deleteTasksById_shouldDeleteInProgressTask() {

        // Arrange
        Integer id = 8;

        Tasks task = createMockTask(
                id, "Tarea en progreso", "Prueba de eliminación",
                TaskStatus.InProgress
        );

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        // Act
        taskService.deleteTasksById(id);

        // Assert
        verify(taskRepository).delete(task);
    }

    // ==================================================
    // TEST 11: IMPEDIR ELIMINAR TAREA COMPLETED
    // ==================================================

    @Test
    void deleteTasksById_shouldRejectCompletedTask() {

        // Arrange
        Integer id = 9;

        Tasks task = createMockTask(
                id, "Tarea finalizada", "No debe eliminarse",
                TaskStatus.Completed
        );

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> taskService.deleteTasksById(id)
        );

        verify(taskRepository, never()).delete(any(Tasks.class));
    }

    // ==================================================
    // TEST 12: ELIMINAR TAREA INEXISTENTE
    // ==================================================

    @Test
    void deleteTasksById_shouldRejectNonexistentTask() {

        // Arrange
        Integer id = 999;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> taskService.deleteTasksById(id)
        );

        verify(taskRepository, never()).delete(any(Tasks.class));
    }

    // ==================================================
    // MÉTODO AUXILIAR PARA CREAR ENTIDADES DE PRUEBA
    // ==================================================

    private Tasks createMockTask(
            Integer id,
            String title,
            String description,
            TaskStatus status) {

        Tasks task = new Tasks();

        ReflectionTestUtils.setField(task, "id", id);

        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setCreationDate(OffsetDateTime.now(ZoneOffset.UTC));

        return task;
    }
}
