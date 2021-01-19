package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SectionDao {
    public static final String SAVE_SQL = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
    public static final String DELETE_SQL = "delete from SECTION where id = ?";
    public static final String FIND_BY_LINE_ID_SQL = "select * from SECTION where line_id = ?";
    public static final String UPDATE_SQL = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
    public static final String FIND_BY_STATION_ID_AND_LINE_ID_SQL = "select * from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
    public static final String COUNT_BY_LINE_ID_SQL = "select count(*) from SECTION where line_id = ?";

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    SectionMapper sectionMapper;

    public void save(Section section) {
        jdbcTemplate.update(SAVE_SQL, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query(FIND_BY_LINE_ID_SQL, sectionMapper, lineId);
    }

    public void update(Section updateSection) {
        jdbcTemplate.update(UPDATE_SQL, updateSection.getUpStationId(), updateSection.getDownStationId(), updateSection.getDistance(), updateSection.getId());
    }

    public List<Section> findByStationIdAndLineId(Long stationId, Long lineId) {
        return jdbcTemplate.query(FIND_BY_STATION_ID_AND_LINE_ID_SQL, sectionMapper, lineId, stationId, stationId);
    }

    public int countByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(COUNT_BY_LINE_ID_SQL, Integer.class, lineId);
    }
}
