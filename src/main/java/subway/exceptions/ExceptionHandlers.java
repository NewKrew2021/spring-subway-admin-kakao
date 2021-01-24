package subway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.lineExceptions.LineDuplicatedException;
import subway.exceptions.lineExceptions.LineNotFoundException;
import subway.exceptions.lineExceptions.LineNothingToUpdateException;
import subway.exceptions.stationExceptions.*;
import subway.exceptions.sectionExceptions.SectionDeleteException;
import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.exceptions.sectionExceptions.SectionNoStationException;
import subway.exceptions.sectionExceptions.SectionSameSectionException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity lineNotFoundHandle(LineNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineDuplicatedException.class)
    public ResponseEntity lineDuplicatedHandle(LineDuplicatedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineNothingToUpdateException.class)
    public ResponseEntity lineNothingToUpdateHandle(LineNothingToUpdateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SectionNoStationException.class)
    public ResponseEntity SectionNoStationHandle(SectionNoStationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(SectionSameSectionException.class)
    public ResponseEntity SectionSameStationHandle(SectionSameSectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(SectionDeleteException.class)
    public ResponseEntity SectionDeleteHandle(SectionDeleteException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(SectionIllegalDistanceException.class)
    public ResponseEntity SectionIllegalDistanceHandle(SectionIllegalDistanceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(StationDuplicateException.class)
    public ResponseEntity StationDuplicateHandle(StationDuplicateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(StationDeleteException.class)
    public ResponseEntity StationDeleteHandle(StationDeleteException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
