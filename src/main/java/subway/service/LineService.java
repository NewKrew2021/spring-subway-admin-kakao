package subway.service;

import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.domain.Section;

import java.util.List;

public interface LineService {
    Line save(Line line, Section section);

    boolean deleteById(Long lineId);

    List<Line> findAll();

    Line findOne(Long lineId);

    boolean update(Line line);

    boolean updateAll(Line line);

    LineResponse saveAndResponse(LineRequest lineRequest);

    List<LineResponse> findAllResponse();

    LineResponse findOneResponse(Long lineId);
}
