package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();
    private static LineDao lineDao;

    private LineDao() {}

    public static LineDao getInstance() {
        if (lineDao == null) {
            lineDao = new LineDao();
        }
        return lineDao;
    }

    public Line save(Line line) {
        System.out.println(line.getName());
        if (isExist(line)) {
            System.out.println("이미 존재함.");
            return null;
        }
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private boolean isExist(Line line) {
        return lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList())
                .contains(line.getName());
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

    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updateById(Long id, Line line) {
        Line updateLine = findById(id);
        Field nameField = ReflectionUtils.findField(Line.class, "name");
        Field colorField = ReflectionUtils.findField(Line.class, "color");
        nameField.setAccessible(true);
        colorField.setAccessible(true);
        ReflectionUtils.setField(nameField, updateLine, line.getName());
        ReflectionUtils.setField(colorField, updateLine, line.getColor());
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }
}
