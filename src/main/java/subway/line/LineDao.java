package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.station.Station;
import subway.station.StationDao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class LineDao {
    private JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    public Line save(Line line) {
        List<Line> lines = findLineByName(line.getName());
        if (lines.size() > 0) {
            return null;
        }
        String sql = "insert into line (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findLineById(Long id) {
        String sql = "select id, name, color from line where id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public List<Line> findLineByName(String name){
        String sql = "select id, name, color from line where name = ?";
        return jdbcTemplate.query(sql, lineRowMapper, name);
    }

    public void updateById(Long id, Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, Long.valueOf(id));
    }

//    public void updateStation(Section section){
//        Line line = lines.stream()
//                .filter(line1 -> line1.getId().equals(section.getLineId()))
//                .findFirst()
//                .orElse(null);
//        List<Station> stations = line.getStations();
//
//        for(int i = 0 ; i< stations.size(); i++){
//            if(stations.get(i).getId() == section.getUpStationId()){
//                sectionDao.addSection(i, stations.get(i).getId(), section);
//                if(i == stations.size() -1){
//                    stations.add(i, stationDao.findById(section.getDownStationId()));
//                    line.updateDownStationId(section.getDownStationId());
//                    line.updateDistance(line.getDistance() + section.getDistance());
//                    return;
//                }
//                stations.add(i + 1, stationDao.findById(section.getDownStationId()));
//                return;
//            }
//            if (stations.get(i).getId() == section.getDownStationId()) {
//                sectionDao.addSection(i, stations.get(i).getId(), section);
//                if (i == 0) {
//                    stations.add(0, stationDao.findById(section.getUpStationId()));
//                    line.updateUpStationId(section.getUpStationId());
//                    line.updateDistance(line.getDistance() + section.getDistance());
//                    return;
//                }
//                stations.add(i - 1, stationDao.findById(section.getUpStationId()));
//                return;
//            }
//        }
//        throw new IllegalArgumentException();
//    }

}
