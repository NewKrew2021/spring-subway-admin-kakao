package subway.line;

import org.springframework.util.ReflectionUtils;

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

    public Line getLine(long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny()
                .orElse(null);
    }

    public void editLineById(long id, String name, String color) {
        Line line = getLine(id);
        line.editLine(name, color);
        //System.out.println(lines);
    }

    public void deleteLineById(long id) {
        lines.remove(getLine(id));
        //System.out.println(lines);
    }
}
