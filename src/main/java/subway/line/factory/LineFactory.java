package subway.line.factory;

import subway.line.domain.Line;
import subway.line.dto.LineRequest;

public class LineFactory {
    public static Line makeLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }
}
