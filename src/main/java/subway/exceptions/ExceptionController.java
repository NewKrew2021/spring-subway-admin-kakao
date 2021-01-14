package subway.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.exception.LineDuplicatedException;
import subway.exceptions.exception.LineNotFoundException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity lineNotFoundHandle() {
        return ResponseEntity.badRequest().body("해당 노선을 찾을 수 없습니다");
    }

    @ExceptionHandler(LineDuplicatedException.class)
    public ResponseEntity lineDuplicatedHandle() {
        return ResponseEntity.badRequest().body("중복된 노선입니다");
    }

}
