package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines=new ArrayList<>();

    public Line save(Line line){
        Line persistStaion=createNewObject(line);
        lines.add(persistStaion);
        return persistStaion;
    }
    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public boolean isContainSameName(String name){
        return this.lines
                .stream()
                .anyMatch(line -> line.getName().equals(name));
    }

    public Line findById(Long lineId){
        for (Line line : lines) {
            if (line.getId() == lineId) {
                return line;
            }
        }
        return null;
    }

    public List<Line> findAll(){
        return lines;
    }


}
