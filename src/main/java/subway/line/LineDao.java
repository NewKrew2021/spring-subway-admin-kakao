package subway.line;

import org.springframework.util.ReflectionUtils;

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
        return lines.stream()
                .filter(value -> value.getName().equals(line.getName()))
                .findAny()
                .orElseGet(() -> {
                    Line persistLine = createNewObject(line);
                    lines.add(persistLine);
                    return persistLine;
                });
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
