package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.DuplicateException;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {
    private Long seq = 0L;
    List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        if (hasDuplicateName(line.getName())) {
            throw new DuplicateException();
        }

        Line persistLine = createNewObject(line);
        lines.add(persistLine);

        return persistLine;
    }

    public Line update(Line line){
        deleteById(line.getId());
        return save(line);
    }

    public Optional<Line> findById(Long id){
        return lines.stream()
                .filter(line -> (line.getId() == id))
                .findFirst();
    }

    public List<Line> findAll() {
        return lines;
    }

    public void deleteById(Long id) {
        lines.removeIf(line -> line.getId() == id);
    }

    public boolean hasDuplicateName(String name) {
        for (Line line : lines) {
            if (line.getName().equals(name)) return true;
        }
        return false;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
