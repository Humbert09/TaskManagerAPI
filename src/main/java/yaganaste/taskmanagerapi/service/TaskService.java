package yaganaste.taskmanagerapi.service;

import jakarta.transaction.Transactional;
import yaganaste.taskmanagerapi.dto.TaskCreateDTO;
import yaganaste.taskmanagerapi.dto.TaskResponseDTO;
import yaganaste.taskmanagerapi.dto.TaskUpdateDTO;
import yaganaste.taskmanagerapi.enums.TaskStatus;
import yaganaste.taskmanagerapi.models.Tasks;

import java.util.List;

public interface TaskService {

    //Crea una tarea
    @Transactional
    TaskResponseDTO createTask(TaskCreateDTO dto);

    //Obtiene todas las tareas
    List<TaskResponseDTO> getAllTasks();

    //Devuelve una tarea por id
    TaskResponseDTO getTaskById(Integer id);

    //Borra una tarea a través de su id
    @Transactional
    void deleteTasksById(Integer id);

    //Modifica una tarea por su id
    @Transactional
    Tasks updateTaskById(Integer id, TaskUpdateDTO dto);
}
