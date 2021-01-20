package subway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestException(){
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<String> InvalidSectionException(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
