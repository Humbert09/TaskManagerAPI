package yaganaste.taskmanagerapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yaganaste.taskmanagerapi.dto.TaskCreateDTO;
import yaganaste.taskmanagerapi.dto.TaskResponseDTO;
import yaganaste.taskmanagerapi.dto.TaskUpdateDTO;
import yaganaste.taskmanagerapi.enums.TaskStatus;
import yaganaste.taskmanagerapi.models.Tasks;
import yaganaste.taskmanagerapi.repository.TaskRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl  implements TaskService{

    private final TaskRepository taskRepository;

    //Crea una nueva tarea
    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskCreateDTO dto) {

        Tasks task = new Tasks();

        //Datos obtenidos del usuario
        task.setTitle(dto.getTitle().trim());
        task.setDescription(dto.getDescription());

        //Datos proporcionados por el distema
        task.setStatus(TaskStatus.Pending);
        task.setCreationDate(OffsetDateTime.now(ZoneOffset.UTC));

        taskRepository.save(task);

        TaskResponseDTO response = new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreationDate()
        );

        return response;
    }

    //Obtiene una lista con todos los objetos Tasks de la DB
    @Override
    @Transactional
    public List<TaskResponseDTO> getAllTasks() {
        List<Tasks> tasks = taskRepository.findAll();

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException(
                    "No hay tareas registradas en el sistema."
            );
        }

        return tasks.stream()
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getCreationDate()
                ))
                .toList();
    }

    //Metodo implementado para buscar la tarea como entidad y poder devolverla en un dto
    private Tasks findTaskEntityById(Integer id) {

        if (id == null || id < 1) {
            throw new IllegalArgumentException(
                    "El ID no puede ser nulo ni menor a 1"
            );
        }

        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La tarea con ID " + id + " no fue encontrada."
                ));
    }


    //Obtiene una tarea a través de su id
    @Override
    @Transactional
    public TaskResponseDTO getTaskById(Integer id) {

        Tasks task = findTaskEntityById(id);

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreationDate()
        );
    }

    //Borra una tarea a través de su id
    @Transactional
    @Override
    public void deleteTasksById(Integer id) {
        Tasks tmp = findTaskEntityById(id);

        if(tmp.getStatus() == TaskStatus.Completed) {
            throw new IllegalStateException ("No puedes eliminar una tarea marcada como 'Completed'");
        }

        taskRepository.delete(tmp);
    }

    //Modifica una tarea por su id
    @Transactional
    @Override
    public TaskResponseDTO updateTaskById(Integer id, TaskUpdateDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Los datos de actualización son obligatorios");
        }

        Tasks tmp = findTaskEntityById(id);

        if(tmp.getStatus() == TaskStatus.Completed) {
            throw new IllegalStateException ("No puedes modificar una tarea marcada como 'Completed'");
        }
        if(dto.getDescription() != null){
        tmp.setDescription(dto.getDescription());}
        if(dto.getStatus() != null){
        tmp.setStatus(dto.getStatus());}

        taskRepository.save(tmp);

        TaskResponseDTO response = new TaskResponseDTO(
                tmp.getId(),
                tmp.getTitle(),
                tmp.getDescription(),
                tmp.getStatus(),
                tmp.getCreationDate()
        );

        return response;
    }



}
