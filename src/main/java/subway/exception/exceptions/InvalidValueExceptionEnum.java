package subway.exception.exceptions;

public enum InvalidValueExceptionEnum {

    INVALID_DISTANCE("추가될 구간의 거리가 기존 노선 거리보다 깁니다."),
    ALREADY_EXIST_STATION("두 역 모두 노선에 이미 존재합니다."),
    NOTHING_EXIST_STATION("두 역 모두 노선에 존재하지 않습니다."),
    ALONE_SECTION("구간이 하나이기 때문에 삭제할 수 없습니다.");

    private String errMessage;

    InvalidValueExceptionEnum(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
