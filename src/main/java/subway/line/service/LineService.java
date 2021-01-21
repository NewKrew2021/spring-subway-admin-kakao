package subway.line.service;

import subway.line.entity.Line;
import subway.line.entity.Lines;

public interface LineService {
    Line create(Line line);

    Line findLineById(Long id);

    Lines findAllLines();

    void update(Line line);

    void delete(Long id);
}
