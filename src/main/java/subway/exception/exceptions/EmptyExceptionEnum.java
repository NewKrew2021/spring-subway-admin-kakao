package subway.exception.exceptions;

public enum EmptyExceptionEnum {

    NOT_FOUND_LINE("노선을 찾을 수 없습니다.");

    private String errMessage;

    EmptyExceptionEnum(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
