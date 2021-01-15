package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class LineDao {
    private Long seq = 0L;
    private final List<Line> lines = new LinkedList<>();

    public Line save(Line line) {
        if (lines.contains(line)) {
            return null;
        }

        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    public Line findOne(Long findId) {
        return lines.stream()
                .filter(line -> line.getId().equals(findId))
                .findFirst()
                .orElse(null);
    }

    public List<Line> findAll() {
        return lines;
    }

    public Line update(Long id, LineRequest lineRequest) {
        Line line = lines.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (line == null) {
            return null;
        }

        Field field1 = ReflectionUtils.findField(Line.class, "name");
        field1.setAccessible(true);
        ReflectionUtils.setField(field1, line, lineRequest.getName());

        Field field2 = ReflectionUtils.findField(Line.class, "color");
        field2.setAccessible(true);
        ReflectionUtils.setField(field2, line, lineRequest.getColor());

        return line;
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
