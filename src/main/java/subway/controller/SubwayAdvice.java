package subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class SubwayAdvice {
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> duplicateNameHandler(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
