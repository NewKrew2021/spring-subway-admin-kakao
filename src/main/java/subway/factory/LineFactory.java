package subway.factory;

import subway.domain.Line;
import subway.dto.LineRequest;

public class LineFactory {
    public static Line getLine(LineRequest lineRequest){
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
    }

    public static Line getLine(Long lineId, LineRequest lineRequest){
        return new Line(lineId, lineRequest.getName(), lineRequest.getColor());
    }
}
