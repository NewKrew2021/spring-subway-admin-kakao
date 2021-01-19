package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SectionDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    SectionMapper sectionMapper;

    public void save(Section section) {
        jdbcTemplate.update("insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public List<Section> findAll() {
        String sql = "select * from SECTION";
        return jdbcTemplate.query(sql, sectionMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from SECTION where id = ?",id);
    }

    public Section findById(Long id) {
        String sql = "select * from SECTION where id = ?";
        return jdbcTemplate.queryForObject(sql, sectionMapper, id);
    }

    public List<Section> findByLineId(Long lineId){
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper, lineId);
    }

    public void update(Section updateSection) {
        jdbcTemplate.update("update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?",
                updateSection.getUpStationId(), updateSection.getDownStationId(), updateSection.getDistance(), updateSection.getId());
    }

    public List<Section> findByStationIdAndLineId(Long stationId, Long lineId) {
        String sql = "select * from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        return jdbcTemplate.query(sql, sectionMapper, lineId, stationId, stationId);
    }
}
