package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.section.Section;
import subway.station.Station;
import subway.station.StationDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LineDao {
    private static LineDao lineDao;
    private Long seq = 0L;
    private List<Line> lines =new ArrayList<>();

    JdbcTemplate jdbcTemplate;
    StationDao stationDao;

    public LineDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line){
        Line persistStation=createNewObject(line);
        lines.add(persistStation);
        return persistStation;
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

    public void modify(Long id, LineRequest lineRequest){
        Line line = findById(id);
        line.modify(lineRequest);
    }

    public void delete(Long id){
        lines.removeIf(it -> it.getId()==id);
    }

    public List<Station> getStations(Line line){
        List<Station> stations = new ArrayList<>();
        stations.add(stationDao.findById(line.getUpStationId()));
        for (Section sections : line.getSections()) {
            stations.add(stationDao.findById(sections.getDownStationId()));
        }
        return stations;
    }

}
