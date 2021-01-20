package subway.line.service;

import subway.line.vo.Line;
import subway.line.vo.Lines;

public interface LineService {
    Line create(Line line);

    Line findLineById(Long id);

    Lines findAllLines();

    void update(Line line);

    void delete(Long id);
}
