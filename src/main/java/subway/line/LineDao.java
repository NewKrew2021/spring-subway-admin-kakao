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

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public List<Line> findAll(){
        return lines;
    }

    public Line findOne(Long lineId){
        return lines.stream().filter(line -> line.getId().equals(lineId)).findFirst().get();
    }
}
