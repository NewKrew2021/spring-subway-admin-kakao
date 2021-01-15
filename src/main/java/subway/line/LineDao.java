package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exceptions.exception.LineDuplicatedException;
import subway.exceptions.exception.LineNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineDao {
    private static LineDao lineDao = new LineDao();

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public static void clear() {
        getInstance().lines.clear();
        getInstance().seq = 0L;
    }

    public Line save(Line line) {
        Line foundLine = lines.stream()
                .filter(tmpLine -> tmpLine.getName().equals(line.getName()))
                .findAny()
                .orElse(null);
        if (foundLine != null)
            throw new LineDuplicatedException();
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

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public List<Line> findAll() {
        return lines;
    }

    public List<LineResponse> getLineResponses() {
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationResponses()))
                .collect(Collectors.toList());
    }

    public LineResponse getLineResponseById(Long lineId) {
        return lines.stream().filter(line -> line.getId().equals(lineId))
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationResponses()))
                .findAny().orElseThrow(LineNotFoundException::new);
    }

    public Line getLineById(Long lineId) {
        return lines.stream().filter(line -> line.getId().equals(lineId))
                .findAny().orElseThrow(LineNotFoundException::new);
    }

    public static LineDao getInstance() {
        return lineDao;
    }

    @Override
    public String toString() {
        return "lineDao{" +
                "seq=" + seq +
                ", liness=" + lines.stream().map(Line::toString).collect(Collectors.joining(", ")) +
                '}';
    }

}
