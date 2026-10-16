package yaganaste.taskmanagerapi.dto;

import yaganaste.taskmanagerapi.enums.TaskStatus;

import java.time.OffsetDateTime;

public class TaskResponseDTO {

    private final Integer id;

    private final String title;

    private final String description;

    private final TaskStatus status;

    private final OffsetDateTime creationDate;

    public TaskResponseDTO(Integer id, String title, String description, TaskStatus status, OffsetDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.creationDate = creationDate;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
}
