package subway.exception;

public class TooLongDistanceSectionException extends RuntimeException {

    private static final String TOO_LONG_DISTANCE_INPUT_EXCEPTION = "추가하려는 구간이 기존 역 구간 사이보다 깁니다.";

    public TooLongDistanceSectionException() {
        super(TOO_LONG_DISTANCE_INPUT_EXCEPTION);
    }

}
