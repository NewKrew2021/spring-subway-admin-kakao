package subway.line.dao;

import subway.line.entity.Line;
import subway.line.entity.Lines;

import java.util.Optional;

public interface LineDao {
    Line insert(String name, String color);

    Optional<Line> findLineById(Long id);

    Optional<Lines> findAllLines();

    int update(Line line);

    int delete(Long id);
}
