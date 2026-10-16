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
    public List<Tasks> getAllTasks() {
        return taskRepository.findAll();
    }

    //Obtiene una tarea a través de su id
    @Override
    public Tasks getTaskById(Integer id) {

        if(id == null || id < 1) {
            throw new IllegalArgumentException("El Id no puede estar vacío ni contener valores menores a 1");
        }

        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La tarea con ID " + id + " no fue encontrada."));
    }

    //Borra una tarea a través de su id
    @Transactional
    @Override
    public void deleteTasksById(Integer id) {
        Tasks tmp = getTaskById(id);

        if(tmp.getStatus() == TaskStatus.Completed) {
            throw new IllegalStateException ("No puedes eliminar una tarea marcada como 'Completed'");
        }

        taskRepository.delete(tmp);
    }

    //Modifica una tarea por su id
    @Transactional
    @Override
    public Tasks updateTaskById(Integer id, TaskUpdateDTO dto) {

        Tasks tmp = getTaskById(id);

        if(tmp.getStatus() == TaskStatus.Completed) {
            throw new IllegalStateException ("No puedes modificar una tarea marcada como 'Completed'");
        }
        if(dto.getDescription() != null){
        tmp.setDescription(dto.getDescription());}
        if(dto.getStatus() != null){
        tmp.setStatus(dto.getStatus());}

        return taskRepository.save(tmp);
    }



}
