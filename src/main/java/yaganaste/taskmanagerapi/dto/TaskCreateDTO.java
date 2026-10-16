package yaganaste.taskmanagerapi.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCreateDTO {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 250, message = "Límite de caracteres excedido")
    private String title;

    private String description;

    public TaskCreateDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public TaskCreateDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
