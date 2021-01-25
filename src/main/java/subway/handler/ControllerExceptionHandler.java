package subway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.*;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(LineNotFoundException.class)
    protected ResponseEntity<Void> handleLineNotFoundException(LineNotFoundException e){
        logger.error("handleLineNotFoundException", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(DuplicateLineNameException.class)
    protected ResponseEntity<Void> handleDuplicateLineNameException(DuplicateLineNameException e){
        logger.error("handleDuplicateLineNameException", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidSectionInsertException.class)
    protected ResponseEntity<Void> handleInvalidSectionInsertException(InvalidSectionInsertException e){
        logger.error("handleInvalidSectionInsertException", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(NotEnoughLengthToDeleteSectionException.class)
    protected ResponseEntity<Void> handleNotEnoughLengthToDeleteSectionException(NotEnoughLengthToDeleteSectionException e){
        logger.error("NotEnoughLengthToDeleteSectionException", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    protected ResponseEntity<Void> handleStationNotFoundException(StationNotFoundException e){
        logger.error("handleStationNotFoundException", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateStationNameException.class)
    protected ResponseEntity<Void> handleDuplicateStationNameException(DuplicateStationNameException e){
        logger.error("handleDuplicateStationNameException", e);
        return ResponseEntity.badRequest().build();
    }


}
