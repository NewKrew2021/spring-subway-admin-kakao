package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private Line updateOldObject(Line sourceLine, Line destLine) {
        Arrays.stream(Line.class.getDeclaredFields())
                .filter(field -> !field.getName().equals("id"))
                .forEach(field -> {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field,
                            destLine,
                            ReflectionUtils.getField(field, sourceLine));
                });
        return destLine;
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

    public Line update(Long id, Line sourceLine) {
        Line destLine = findOne(id);
        return updateOldObject(sourceLine, destLine);
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public void deleteAll() {
        lines.clear();
    }
}
