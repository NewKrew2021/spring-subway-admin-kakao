package subway.line;

import subway.section.Section;

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
