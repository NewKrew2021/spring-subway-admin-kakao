package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.line.LineRequest;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insertOnCreateLine(Long lineId, Long upStationId, Long downStationId, int distance) {
        if (upStationId == downStationId) {
            return false;
        }

        insertSection(lineId, upStationId, 0);
        insertSection(lineId, downStationId, distance);

        return true;
    }

    public boolean insert(Long lineId, Long upStationId, Long downStationId, int distance) {
        Sections sections = findByLineId(lineId);

        Section newSection = sections.insert(upStationId, downStationId, distance);
        if (newSection == null) {
            return false;
        }

        insertSection(newSection.getLineId(), newSection.getStationId(), newSection.getDistance());
        return true;
    }

    public boolean delete(Long lineId, Long stationId) {
        Sections sections = findByLineId(lineId);
        if (sections.hasOnlyTwoSections()) {
            return false;
        }

        deleteSection(lineId, stationId);
        return true;
    }

    private boolean insertSection(Long lineId, Long stationId, int distance) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        return jdbcTemplate.update(sql, lineId, stationId, distance) > 0;
    }

    private boolean deleteSection(Long lineId, Long stationId) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        return jdbcTemplate.update(sql, lineId, stationId) > 0;
    }

    public Sections findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
