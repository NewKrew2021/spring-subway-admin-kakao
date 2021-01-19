package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DistanceException extends IllegalArgumentException{
    public DistanceException() {
        super("잘못된 거리 입니다.");
    }
}
