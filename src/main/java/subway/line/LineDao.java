package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public void save(Line line) {
        this.lines.add(this.createNewObject(line));
    }

    public boolean hasContains(Line line) {
        return this.lines.contains(line);
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }


    public List<Line> getLines() {
        return lines;
    }
}