package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public List<Line> findAll() { return lines; }

    public Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Line findByName(String name) {
        return lines.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void update(Line originLine, Line updateLine) {
        Field field = ReflectionUtils.findField(Line.class, "name");
        field.setAccessible(true);
        ReflectionUtils.setField(field, originLine, updateLine.getName());

        field = ReflectionUtils.findField(Line.class, "color");
        field.setAccessible(true);
        ReflectionUtils.setField(field, originLine, updateLine.getColor());

        field = ReflectionUtils.findField(Line.class, "stations");
        field.setAccessible(true);
        ReflectionUtils.setField(field, originLine, updateLine.getStations());
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);

        field = ReflectionUtils.findField(Line.class, "stations");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, new ArrayList<Station>());

        return line;
    }
}
