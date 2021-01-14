package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exceptions.DuplicateLineNameException;
import subway.station.StationResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {
    private static long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private static boolean isExistsLineName(Line line) {
        return lines.contains(line);
    }

    public static Line save(Line line) {
        if(isExistsLineName(line)) {
            throw new DuplicateLineNameException("중복된 이름의 노선입니다.");
        }
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public static Line saveSection(Long id, Section section) {
        Line line = findById(id).get();
        line.save(section);
        return line;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line updateLine(Long id, Line newLine) {
        Optional<Line> line = findById(id);
        if(line == null) {
            throw new IllegalArgumentException();
        }
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, newLine, id);
        lines.set(lines.indexOf(line.get()), newLine);
        return newLine;
    }

    public static boolean deleteById(Long id) {
        return lines.removeIf(it -> it.getId().equals(id));
    }

    public static void deleteSectionById(Long lineId, Long stationId) {
        Line line = findById(lineId).get();
        line.delete(stationId);
    }

    public static List<StationResponse> getStationResponsesById(Long id) {
        Line line = findById(id).get();
        return line.getStationResponses();
    }
}
