package subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class ExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(Exception e) {
        logExceptionInfo(e);
        return ResponseEntity.badRequest().body("잘못된 입력입니다.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(Exception e) {
        logExceptionInfo(e);
        return ResponseEntity.badRequest().body("잘못된 입력입니다.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKeyException(Exception e) {
        logExceptionInfo(e);
        return ResponseEntity.badRequest().body("중복된 입력입니다.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(Exception e) {
        logExceptionInfo(e);
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidExceptionException(Exception e) {
        logExceptionInfo(e);
        return ResponseEntity.badRequest().body("잘못된 입력입니다.");
    }

    private void logExceptionInfo(Exception e) {
        logger.warn(e.getMessage());
        logger.warn(e.getClass().toString() + " / " + Arrays.toString(e.getSuppressed()));
    }
}
