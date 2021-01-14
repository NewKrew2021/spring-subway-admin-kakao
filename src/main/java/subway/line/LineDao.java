package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineDao {
    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    public Line save(Line line) throws SQLException {
        Line persistStation = createNewObject(line);

        if (isExists(line)) {
            throw new SQLException();
        }

        lines.add(persistStation);
        return persistStation;
    }

    private boolean isExists(Line line) {
        return lines.stream()
                .anyMatch(lineIn -> line.getName().equals(lineIn.getName()));
    }

    public List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public Line getById(Long id) {
        return lines.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst().orElse(null);
    }

    // TODO 변경할 이름이 이미 존재할 경우
    public void update(Long id, Line line) throws SQLException {
        Line selectedLine = getById(id);

        if (selectedLine == null) {
            throw new SQLException();
        }

        selectedLine.setName(line.getName());
        selectedLine.setColor(line.getColor());
    }

    public boolean deleteById(Long id) {
        return lines.removeIf(it -> it.getId().equals(id));
    }
}
