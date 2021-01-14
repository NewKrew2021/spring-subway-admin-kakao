package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
public class ExceptionAdvice {

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity handle() {
        return ResponseEntity.badRequest().body("Exception");
    }

}
