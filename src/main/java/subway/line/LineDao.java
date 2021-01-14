package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private static final LineDao instance = new LineDao();
    private static final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    private LineDao() {
    }

    public static LineDao getInstance() {
        return instance;
    }

    public Line save(Line line) {
        lines.stream()
                .filter(value -> value.getName().equals(line.getName()))
                .findAny()
                .ifPresent(val -> {
                    throw new DuplicateNameException(line.getName());
                });
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public List<Line> findAll() {
        return lines;
    }

    public Line findOne(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny()
                .orElseGet(() -> {
                    throw new NoContentException(id + "(Line)");
                });
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public void deleteAll() {
        lines.clear();
    }
}
