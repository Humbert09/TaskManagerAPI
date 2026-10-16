package yaganaste.taskmanagerapi.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yaganaste.taskmanagerapi.dto.ErrorResponseDTO;
import org.springframework.http.converter.HttpMessageNotReadableException;
import tools.jackson.databind.exc.MismatchedInputException;
import yaganaste.taskmanagerapi.enums.TaskStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO>
            handleIllegalArgumentException(IllegalArgumentException e){
        ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO>
            handleEntityNotFoundException(EntityNotFoundException e){
        ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(),
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO>
            handleIllegalStateException(IllegalStateException e){
        ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(),
                HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Los datos proporcionados no son válidos";

        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {

        String message = "El JSON enviado contiene datos inválidos.";

        Throwable cause = e.getCause();

        while (cause != null) {

            if (cause instanceof MismatchedInputException error
                    && error.getTargetType() == TaskStatus.class) {

                message = "Status inválido. Pruebe con estos status: "
                        + "Pending, InProgress, Completed";
                break;
            }

            cause = cause.getCause();
        }

        ErrorResponseDTO response = new ErrorResponseDTO(
                message,
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
