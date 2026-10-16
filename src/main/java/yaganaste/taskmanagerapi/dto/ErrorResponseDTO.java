package yaganaste.taskmanagerapi.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ErrorResponseDTO {

        private String message;
        private int status;
        private OffsetDateTime timestamp;

        public ErrorResponseDTO(String message, int status) {
            this.message = message;
            this.status = status;
            this.timestamp = OffsetDateTime.now();
        }

        // Getters y Setters
        public String getMessage() { return message; }
        public int getStatus() { return status; }
        public OffsetDateTime getTimestamp() { return timestamp; }
}
