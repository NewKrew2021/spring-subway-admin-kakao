package subway.service;

import subway.domain.Line;
import subway.domain.Sections;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.domain.Section;
import subway.dto.LineResponseWithStation;

import java.util.List;

public interface LineService {
    Line save(Line line, Section section);

    void deleteById(Long lineId);

    List<Line> findAll();

    Line findOne(Long lineId);

    void update(Line line);

    void deleteSection(Long lineId, Long stationId);

    void saveSection(Section section);

    void saveSection(Line line, Section section);
}
