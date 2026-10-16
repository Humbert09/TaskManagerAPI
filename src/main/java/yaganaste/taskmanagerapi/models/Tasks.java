package yaganaste.taskmanagerapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import yaganaste.taskmanagerapi.enums.TaskStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", length = 250, nullable = false)
    private String title;


    @Column(columnDefinition = "text", name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TaskStatus status;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;

    public Tasks( String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Tasks() {
    }

    public int getId() {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setTitle(String trim) {
    }

    public void setCreationDate(OffsetDateTime now) {
    }
}
