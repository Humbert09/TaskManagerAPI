package yaganaste.taskmanagerapi.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import yaganaste.taskmanagerapi.dto.TaskCreateDTO;
import yaganaste.taskmanagerapi.dto.TaskResponseDTO;
import yaganaste.taskmanagerapi.service.TaskService;

import java.net.URI;

@RestController
@RequestMapping("/taskmanagement")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @PostMapping("/task")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskCreateDTO dto){
       TaskResponseDTO response = taskService.createTask(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
