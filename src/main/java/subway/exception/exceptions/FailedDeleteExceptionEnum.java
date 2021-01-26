package subway.exception.exceptions;

public enum FailedDeleteExceptionEnum {

    FAIL_DELETE_LINE("노선을 삭제할 수 없습니다."),
    FAIL_DELETE_SECTIONS("노선 내 모든 구간 정보를 삭제할 수 없습니다.");

    private String errMessage;

    FailedDeleteExceptionEnum(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
