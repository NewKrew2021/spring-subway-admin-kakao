package subway.line;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        if (lines.stream().anyMatch(l -> l.getName().equals(line.getName()))) {
            return null;
        }
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

    public Line findOne(Long lineId) {
        return lines.stream().filter(line -> line.getId().equals(lineId)).findFirst().get();
    }

    public void update(Line line) {
        lines.remove(findOne(line.getId()));
        lines.add(line);
    }

    public boolean saveSection(Long lineId, Section section) {
        Line line = lines.stream().filter(l -> l.getId().equals(lineId)).findFirst().get();
        List<Section> sections = line.getSections();
        int sectionIdx = -1;
        boolean upFlag = false, downFlag = false;
        Long upId = section.getUpStationId(), downId = section.getDownStationId(), stationId = -1L;
        for (int i = 0; i < sections.size(); i++) {
            if (upId == sections.get(i).getUpStationId() || upId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = upId;
                upFlag = true;
            }

            if (downId == sections.get(i).getUpStationId() || downId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = downId;
                downFlag = true;
            }
        }
        if (upFlag && downFlag) {
            return false;
        }
        if (sectionIdx == -1) {
            return false;
        }
        if (stationId == upId) {
            sections.add(sectionIdx, section);
            if (sectionIdx < sections.size() - 1) {
                Long nextStationId = sections.get(sectionIdx + 1).getDownStationId();
                Integer nextStationDistance = sections.get(sectionIdx + 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx + 1);
                sections.add(sectionIdx + 1, new Section(downId, nextStationId, nextStationDistance));
            }
        }

        if (stationId == downId) {
            sections.add(sectionIdx, section);
            if (sectionIdx > 0) {
                Long prevStationId = sections.get(sectionIdx - 1).getDownStationId();
                Integer prevStationDistance = sections.get(sectionIdx - 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx - 1);
                sections.add(sectionIdx - 1, new Section(prevStationId, upId, prevStationDistance));
            }
        }
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Line line = findOne(lineId);
        List<Section> sections = line.getSections();
        if (sections.size() == 1) {
            return false;
        }
        int upIdx = -1, downIdx = -1;

        boolean upFlag = false, downFlag = false;
        for (int i = 0; i < sections.size(); i++) {
            if (stationId == sections.get(i).getUpStationId()) {
                upIdx = i;
                upFlag = true;
            }

            if (stationId == sections.get(i).getDownStationId()) {
                downIdx = i;
                downFlag = true;
            }
        }
        if (!upFlag && !downFlag) {
            return false;
        } else if (upFlag && downFlag) {
            Long nextStationId = sections.get(upIdx).getDownStationId();
            Integer nextDistance = sections.get(upIdx).getDistance();
            Long prevStationId = sections.get(downIdx).getUpStationId();
            Integer prevDistance = sections.get(downIdx).getDistance();

            sections.remove(upIdx);
            sections.remove(downIdx);
            sections.add(downIdx, new Section(prevStationId, nextStationId, nextDistance + prevDistance));
        } else if (upFlag) {
            sections.remove(upIdx);
        } else if (downFlag) {
            sections.remove(downIdx);
        }

        return true;
    }
}
