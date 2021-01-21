package subway.line;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(Long lineId) {
        super(lineId + "에 해당하는 라인이 없습니다.");
    }
}
