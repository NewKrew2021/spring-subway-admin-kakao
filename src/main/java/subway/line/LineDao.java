package subway.line;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@Repository
public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    public List<Line> findAll() {
        return lines;
    }

    public Optional<Line> findByName(String name) {
         return lines.stream()
                .filter(line -> line.getName().equals(name))
                .findAny();
    }

    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
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

    public void modify(Long id, LineRequest lineRequest) {
        Line line = lines.stream()
                .filter(l -> l.getId().equals(id))
                .findAny()
                .get();
        line.setName(lineRequest.getName());
        line.setColor(lineRequest.getColor());
    }

    public void addSection(Long id, Section section) {
        Line line = lines.stream()
                .filter(l -> l.getId().equals(id))
                .findAny()
                .get();
        line.getSections().add(section);
    }
}
