package subway.common.error;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus status;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(ErrorStatus status, String message) {
        return new ErrorResponse(status.getHttpStatus(), message);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
