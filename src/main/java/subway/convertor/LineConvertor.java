package subway.convertor;

import subway.domain.Line;
import subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineConvertor {
    public static LineResponse convertLine(Line line) {
        return new LineResponse(line.getId(),line.getName(),line.getColor());
    }

    public static List<LineResponse> convertLines(List<Line> lines) {
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getName()))
                .collect(Collectors.toList());
    }
}
