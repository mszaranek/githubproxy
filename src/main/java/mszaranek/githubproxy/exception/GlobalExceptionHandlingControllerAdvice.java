package mszaranek.githubproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {


    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseStatusException> handleWebClientResponseException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ResponseStatusException(ex.getStatusCode().value(), ex.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseStatusException> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ResponseStatusException(ex.getStatus().value(), ex.getMessage()));
    }
}
