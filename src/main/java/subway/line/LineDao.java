package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistStation = createNewObject(line);
        if(lines.contains(line)){
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
        lines.add(persistStation);
        return persistStation;
    }

    public List<Line> findAll() {
        return lines;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = findById(id).get();
        line.update(lineRequest);
    }
}
