package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private static LineDao instance;

    public static LineDao getInstance(){
        if(instance == null)
            instance = new LineDao();
        return instance;
    }
    private LineDao(){}

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public List<Line> findAll() {
        return lines;
    }

    public Line findById(Long id) {
        return lines.stream()
                .filter(val -> val.getId()==id)
                .collect(Collectors.toList()).get(0);
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
