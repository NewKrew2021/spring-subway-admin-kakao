package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;
import subway.station.StationDao;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class LineDao {
    private static LineDao lineDao;
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();
    private StationDao stationDao;
    private SectionDao sectionDao;

    private LineDao() {
        stationDao = StationDao.getInstance();
        sectionDao = SectionDao.getInstance();
    }

    public static LineDao getInstance() {
        if (lineDao == null) {
            lineDao = new LineDao();
        }
        return lineDao;
    }

    public Line save(Line line) {
        if (isExist(line)) {
            return null;
        }
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }


    private boolean isExist(Line line) {
        return lines.stream()
                .anyMatch(line1 -> line1.getName().equals(line.getName()));
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
        lines.removeIf(line -> line.getId().equals(id));
    }

    public void updateStation(Section section){
        Line line = lines.stream()
                .filter(line1 -> line1.getId().equals(section.getLineId()))
                .findFirst()
                .orElse(null);
        List<Station> stations = line.getStations();

        for(int i = 0 ; i< stations.size(); i++){
            if(stations.get(i).getId() == section.getUpStationId()){
                sectionDao.addSection(i, stations.get(i).getId(), section);
                if(i == stations.size() -1){
                    stations.add(i, stationDao.findById(section.getDownStationId()));
                    line.updateDownStationId(section.getDownStationId());
                    line.updateDistance(line.getDistance() + section.getDistance());
                }
                stations.add(i + 1, stationDao.findById(section.getDownStationId()));
                return;
            }
            if (stations.get(i).getId() == section.getDownStationId()) {
                sectionDao.addSection(i, stations.get(i).getId(), section);
                if (i - 1 < 0) {
                    stations.add(0, stationDao.findById(section.getUpStationId()));
                    line.updateUpStationId(section.getUpStationId());
                    line.updateDistance(line.getDistance() + section.getDistance());
                }
                stations.add(i - 1, stationDao.findById(section.getUpStationId()));
                return;
            }
        }
        throw new IllegalArgumentException();
    }
}
