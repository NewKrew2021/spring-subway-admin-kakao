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

    public static ErrorResponse of(Exception e) {
        return new ErrorResponse(
                ErrorStatus.of(e)
                        .getHttpStatus(),
                e.getMessage()
        );
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
