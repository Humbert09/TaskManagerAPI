package yaganaste.taskmanagerapi.dto;

import yaganaste.taskmanagerapi.enums.TaskStatus;
import yaganaste.taskmanagerapi.models.Tasks;

public class TaskUpdateDTO {

    private String description;

    private TaskStatus status;

    public TaskUpdateDTO(String description, TaskStatus status) {
        this.description = description;
        this.status = status;
    }

    public TaskUpdateDTO() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
