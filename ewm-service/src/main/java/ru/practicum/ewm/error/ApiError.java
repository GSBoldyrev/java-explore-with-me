package ru.practicum.ewm.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {

    String message;
    String reason;
    HttpStatus status;
    LocalDateTime timestamp;
}
