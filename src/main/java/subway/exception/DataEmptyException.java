package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataEmptyException extends RuntimeException{
    public DataEmptyException() {
        super("데이터가 존재하지 않습니다.");
    }
}
