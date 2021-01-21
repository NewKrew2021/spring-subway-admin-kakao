package subway.exception;

public class LineNotFoundException extends EntityNotFoundException {
    public LineNotFoundException(Long id) {
        super(String.format("id=%d인 노선이 존재하지 않습니다", id));
    }
}
