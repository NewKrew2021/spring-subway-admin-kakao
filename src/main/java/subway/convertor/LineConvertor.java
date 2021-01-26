package subway.convertor;

import subway.domain.Line;
import subway.dto.LineResponse;
import subway.dto.LineResponseWithStation;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineConvertor {
    public static LineResponse convertLine(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static List<LineResponse> convertLines(List<Line> lines) {
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public static LineResponseWithStation convertLineWithResponse(Line line) {
        System.out.println("#####################");
        System.out.println(line.getStations());
        return new LineResponseWithStation(line.getId(), line.getName(), line.getColor(), line.getStations()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList()));
    }
}
