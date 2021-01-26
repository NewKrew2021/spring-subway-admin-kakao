package subway.line.domain;

public class LineNotFoundException extends RuntimeException {

    private static final String LINE_NOT_FOUND_MESSAGE = "에 해당하는 라인이 없습니다.";

    public LineNotFoundException(Long lineId) {
        super(lineId + LINE_NOT_FOUND_MESSAGE);
    }
}
