package subway.line.service;

import subway.line.dto.LineResponse;
import subway.line.entity.Line;
import subway.line.entity.Lines;

import java.util.List;

public interface LineService {
    Line create(String name, String color);

    Line getLineById(Long id);

    Lines getAllLines();

    void update(Line line);

    void delete(Long id);

    LineResponse getLineWithStationsByLineId(Long lineId);

    List<LineResponse> getAllLinesWithStations();
}
