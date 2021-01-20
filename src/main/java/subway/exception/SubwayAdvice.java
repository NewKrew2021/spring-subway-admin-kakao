package subway.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.id.InvalidIdException;
import subway.exception.section.SectionException;

@ControllerAdvice
public class SubwayAdvice {
    @ExceptionHandler(value = SectionException.class)
    public ResponseEntity<Void> sectionExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {DataAccessException.class, InvalidIdException.class})
    public ResponseEntity<Void> exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }
}
