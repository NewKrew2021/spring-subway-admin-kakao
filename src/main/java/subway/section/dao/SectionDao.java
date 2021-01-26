package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.section.domain.Section;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> rowMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("station_id"),
            rs.getInt("position")
    );

    public Section save(long stationId, long lineId, int position) {
        String INSERT_SQL = "INSERT INTO section(line_id,station_id,position) VALUES (?,?,?)";
        String SQL = "SELECT * FROM section WHERE station_id = ? AND line_id = ?";
        jdbcTemplate.update(INSERT_SQL, lineId, stationId, position);

        return jdbcTemplate.queryForObject(SQL, rowMapper, stationId, lineId);
    }

    public Section save(Section section) {
        return save(section.getStationId(), section.getLineId(), section.getPosition());
    }


    public void createLineSection(Line newLine, LineRequest lineRequest) {
        save(lineRequest.getUpStationId(), newLine.getId(), 0);
        save(lineRequest.getDownStationId(), newLine.getId(), lineRequest.getDistance());
    }

    public List<Section> getSections(long lineId) {
        String SQL = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(SQL, rowMapper, lineId);
    }


    public void delete(long lineId, long stationId) {
        String SQL = "DELETE FROM section WHERE line_id = ? AND station_id = ?";
        jdbcTemplate.update(SQL, lineId, stationId);
    }
}
