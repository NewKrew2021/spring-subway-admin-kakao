package subway.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity duplicateKey(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(SectionDistanceExceedException.class)
    public ResponseEntity sectionDistanceExceed(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(DeleteSectionException.class)
    public ResponseEntity deleteSection(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgument(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
