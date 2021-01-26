package subway.exception.exceptions;

public enum FailedSaveExceptionEnum {

    EMPTY_LINE_ARGUMENT("모든 정보를 입력해주세요."),
    SAME_STATION("상행종점과 하행종점은 같을 수 없습니다.");

    private String errMessage;

    FailedSaveExceptionEnum(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
