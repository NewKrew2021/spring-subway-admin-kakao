package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exception.NotExistException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public List<Line> findAll() {
        return lines;
    }

    public Line findById(Long id) {
        try {
            return lines.stream().filter(line -> line.getId().equals(id)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
    }

    public boolean existName(String name) {
        return lines.stream().anyMatch(it -> it.getName().equals(name));
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
