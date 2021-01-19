package subway.exception.custom;

public class CannotDeleteSectionException extends RuntimeException {
    public CannotDeleteSectionException() {
        super("노선에서 역을 삭제할 수 없습니다. 노선이 최소 크기이거나, 역이 노선에 포함되지 않습니다.");
    }
}
