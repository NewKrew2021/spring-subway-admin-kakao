package subway.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.exception.*;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineDuplicatedException.class)
    public ResponseEntity lineDuplicatedHandle() {
        return ResponseEntity.badRequest().body("중복된 노선입니다");
    }

    @ExceptionHandler(LineNothingToUpdateException.class)
    public ResponseEntity lineNothingToUpdateHandle() {
        return ResponseEntity.badRequest().body("업데이트가 가능한 노선이 존재하지 않습니다.");
    }
}
