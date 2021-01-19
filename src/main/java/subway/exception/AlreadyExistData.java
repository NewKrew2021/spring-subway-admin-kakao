package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistData extends RuntimeException{
    public AlreadyExistData() {
        super("이미 존재하는 데이터 입니다.");
    }
}
