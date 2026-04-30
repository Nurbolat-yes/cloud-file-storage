package by.nurbolat.cloud_file_storage.exception.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    public ExceptionResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
