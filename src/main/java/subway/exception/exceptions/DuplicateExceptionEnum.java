package subway.exception.exceptions;

public enum DuplicateExceptionEnum {

    DUPLICATE_LINE_NAME("중복된 노선 이름입니다."),
    DUPLICATE_STATION_NAME("중복된 역 이름입니다.");

    private String errMessage;

    DuplicateExceptionEnum(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
