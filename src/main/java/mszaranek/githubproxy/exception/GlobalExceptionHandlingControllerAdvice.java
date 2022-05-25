package mszaranek.githubproxy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ResponseStatusException(ex.getStatus().value(),ex.getMessage()));
    }
}
