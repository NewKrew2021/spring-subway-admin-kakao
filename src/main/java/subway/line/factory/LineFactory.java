package subway.line.factory;

import subway.line.domain.Line;
import subway.line.dto.LineRequest;

public class LineFactory {
    public static Line makeLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public static Line makeLine(long lineId, LineRequest lineRequest) {
        return new Line(lineId, lineRequest.getName(), lineRequest.getColor());
    }
}
