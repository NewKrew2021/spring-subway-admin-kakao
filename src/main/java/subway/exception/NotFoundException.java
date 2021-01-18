package subway.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException{
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public NotFoundException() {
        super("Data not found");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
