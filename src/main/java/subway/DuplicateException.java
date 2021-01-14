package subway;

import org.springframework.http.HttpStatus;

public class DuplicateException extends RuntimeException {
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public DuplicateException(){
        super("Same name exists");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
