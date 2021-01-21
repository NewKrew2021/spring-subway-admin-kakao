package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section) {
        jdbcTemplate.update(
                "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)",
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    public void update(Section section){
        String query = "update section set line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(query, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void deleteById(Long id) {
        String query = "delete from section where id = ?";
        jdbcTemplate.update(query, id);
    }

    public List<Section> findAllByLineId(Long id){
        String query = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapper, id);
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, upstationId가 일치하는 section을 return
     */
    public Section getSectionByUpStationId(Long lineId, Long upStationId) {
        try {
            String sqlQuery = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ? and up_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, sectionRowMapper, lineId, upStationId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, downStationId가 일치하는 section을 return
     */
    public Section getSectionByDownStationId(Long lineId, Long downStationId) {
        try {
            String sqlQuery = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ? and down_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, sectionRowMapper, lineId, downStationId);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean alreadyExistInLine(Long lineId, Long stationId) {
        String sqlQuery = "select count(*) from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        int existCount = jdbcTemplate.queryForObject(sqlQuery, int.class, lineId, stationId, stationId);
        return existCount != 0;
    }
}