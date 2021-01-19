package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.InvalidSectionException;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    private final RowMapper<Section> stationRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
        );
        return section;
    };

    public void save(Long lineId, SectionRequest sectionRequest) {
        String sql = "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)";
        try {
            jdbcTemplate.update(sql, lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        } catch (Exception e) {
            throw new InvalidSectionException("SECTION ERROR");
        }
    }

    public Section findById(Long id) {
        return jdbcTemplate.queryForObject("select id, up_station_id, down_station_id, distance from SECTION where id = ?", sectionRowMapper, id);
    }

    public int countByLineId(Long id) {
        String sql = "select count(*) from section where line_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, Long.valueOf(id));
    }

    public int countByLineIdAndStationId(Long lineId, Long stationId) {
        return jdbcTemplate.queryForObject("select count(*) from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)",
                Integer.class, lineId, stationId, stationId);
    }

    public Long findSectionIdFromEqualUpStationId(Long lineId, Long upStationId) {
        try {
            String sql = "select id from section where line_id = ? and up_station_id = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, Long.valueOf(lineId), Long.valueOf(upStationId));
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long findSectionIdFromEqualDownStationId(Long lineId, Long downStaionId) {
        try {
            String sql = "select id from section where line_id = ? and down_station_id = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, Long.valueOf(lineId), Long.valueOf(downStaionId));
        } catch (Exception e) {
            return 0L;
        }
    }

    public int findDistanceById(Long id) {
        String sql = "select distance from section where id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    public void updateUpStation(Long sectionId, Long newStationId, int newDistance) {
        String sql = "update section set up_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, newStationId, newDistance, sectionId);
    }

    public void updateDownStation(Long sectionId, Long newStationId, int newDistance) {
        String sql = "update section set down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, newStationId, newDistance, sectionId);
    }

    public void updateSection(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void deleteByLineIdAndDownStationId(Long lineId, Long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and down_station_id = ?", lineId, stationId);
    }

    public void deleteByLineIdAndUpStationId(Long lineId, Long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and up_station_id = ?", lineId, stationId);
    }

    public void deleteById(Long lineId, Long stationId) {
        Section upStationSection = jdbcTemplate.queryForObject("select * from SECTION where line_id = ? and up_station_id = ?", sectionRowMapper, lineId, stationId);
        Section downStationSection = jdbcTemplate.queryForObject("select * from SECTION where line_id = ? and down_station_id = ?", sectionRowMapper, lineId, stationId);

        jdbcTemplate.update("delete from SECTION where line_id = ? and up_station_id = ?", lineId, stationId);
        jdbcTemplate.update("delete from SECTION where line_id = ? and down_station_id = ?", lineId, stationId);
        jdbcTemplate.update("insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)",
                lineId, downStationSection.getUpStationId(), upStationSection.getDownStationId(), upStationSection.getDistance() + downStationSection.getDistance());
    }

    public int deleteAllByLineId(Long id) {
        return jdbcTemplate.update("delete from section where line_id = ?", id);
    }

    public List<Section> findAllSections(Long lineId, Long startStationId) {
        return jdbcTemplate.query("WITH RECURSIVE findAllSections(up_station_id, down_station_id) AS" +
                "(" +
                "SELECT up_station_id, down_station_id from SECTION " +
                "where line_id = ? and up_station_id = ? " +
                "union all " +
                "select s.up_station_id, s.down_station_id from SECTION s " +
                "inner join findAllSections on findAllSections.down_station_id = s.up_station_id " +
                ") " +
                "select * from findAllSections",
                stationRowMapper, lineId, startStationId
        );
    }
}
