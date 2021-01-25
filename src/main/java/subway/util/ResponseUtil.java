package subway.util;

import subway.line.domain.Line;
import subway.line.dto.LineResponse;
import subway.station.domain.Station;
import subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseUtil {
    public static List<LineResponse> getLineResponses(List<Line> lines) {
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public static List<StationResponse> getStationResponses(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
