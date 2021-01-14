package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
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

    public void update(Line line){
        lines.remove(findOne(line.getId()));
        lines.add(line);
    }

    public void saveSection(Long lineId, Section section) {
        Line line = lines.stream().filter(l -> l.getId().equals(lineId)).findFirst().get();
        List<Section> sections = line.getSections();
        int sectionIdx = -1;
        Long upId = section.getUpStationId(), downId = section.getDownStationId(), stationId = -1L;
        for (int i = 0; i < sections.size(); i++) {
            if(upId == sections.get(i).getUpStationId() || upId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = upId;
                break;
            }

            if(downId == sections.get(i).getUpStationId() || downId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = downId;
                break;
            }
        }
        if(sectionIdx == -1){
            return;
        }
        if(stationId == upId){
            sections.add(sectionIdx, section);
            if(sectionIdx < sections.size() - 1){
                Long nextStationId = sections.get(sectionIdx + 1).getDownStationId();
                Integer nextStationDistance = sections.get(sectionIdx + 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx + 1);
                sections.add(sectionIdx + 1, new Section(downId, nextStationId, nextStationDistance));
            }
        }

        if(stationId == downId){
            sections.add(sectionIdx, section);
            if(sectionIdx > 0){
                Long prevStationId = sections.get(sectionIdx - 1).getDownStationId();
                Integer prevStationDistance = sections.get(sectionIdx - 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx -1);
                sections.add(sectionIdx - 1, new Section(prevStationId, upId, prevStationDistance));
            }
        }
    }
}
