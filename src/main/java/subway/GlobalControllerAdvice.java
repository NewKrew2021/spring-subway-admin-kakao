package subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.line.exception.LineAlreadyExistException;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(LineAlreadyExistException.class)
    public ResponseEntity lineExistsExceptionHandler(LineAlreadyExistException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
