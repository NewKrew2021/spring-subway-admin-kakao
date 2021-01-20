package subway.line.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.line.exception.AddSectionException;
import subway.line.exception.DuplicateLineException;
import subway.line.exception.DeleteSectionException;

@ControllerAdvice()
public class ExceptionAdvice {
    @ExceptionHandler(AddSectionException.class)
    public ResponseEntity<String> addSectionHandle() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구간을 등록할 수 없습니다.");
    }

    @ExceptionHandler(DeleteSectionException.class)
    public ResponseEntity<String> deleteSectionHandle() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구간을 제거할 수 없습니다.");
    }

    @ExceptionHandler(DuplicateLineException.class)
    public ResponseEntity<String> createLineHandle() {
        return ResponseEntity.badRequest().body("노선을 등록할 수 없습니다.");
    }

}
