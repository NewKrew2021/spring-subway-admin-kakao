package subway.exception.custom;

public class CannotAddSectionException extends RuntimeException {
    public CannotAddSectionException() {
        super("노선에 구간이 이미 있거나, 추가하고자 하는 역들이 노선에 포함되어 있지 않습니다.");
    }
}
