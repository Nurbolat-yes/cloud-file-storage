package by.nurbolat.cloud_file_storage.exception.auth;

import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.exception.global.ExceptionResponse;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.global.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->
                errors.put(error.getField(),error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors,GlobalExceptionHandler.buildHeaders(),status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistException(UserAlreadyExistsException exception){
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse response = new ExceptionResponse(status.value(),exception.getMessage());
        return new ResponseEntity<>(response,GlobalExceptionHandler.buildHeaders(),status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse(status.value(),ex.getMessage());
        return new ResponseEntity<>(response,GlobalExceptionHandler.buildHeaders(),status);
    }

    @ExceptionHandler(EmailOrPasswordIncorrect.class)
    public ResponseEntity<ExceptionResponse> handleEmailOrPasswordIncorrect(EmailOrPasswordIncorrect ex){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse(status.value(),ex.getMessage());
        return new ResponseEntity<>(response,GlobalExceptionHandler.buildHeaders(),status);
    }

}
