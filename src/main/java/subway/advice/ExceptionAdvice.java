package subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.*;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<String> internalServerErrorHandler(InvalidSectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler({InvalidLineArgumentException.class, DuplicateLineNameException.class})
    public ResponseEntity<String> badRequestErrorHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({DuplicateStationNameException.class, InvalidStationArgumentException.class})
    public ResponseEntity<String> errorHandler(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
