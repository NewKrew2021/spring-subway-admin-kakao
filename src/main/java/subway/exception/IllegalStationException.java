package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalStationException extends IllegalArgumentException{
    public IllegalStationException() {
        super("잘못된 역 번호 입니다.");
    }
}
