package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.line.Line;
import subway.line.LineRequest;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(long stationId, long lineId, int distance) {
        String INSERT_SQL = "INSERT INTO section(line_id,station_id,distance) VALUES (?,?,?)";
        String SQL = "SELECT * FROM section WHERE station_id = ? AND line_id = ?";
        jdbcTemplate.update(INSERT_SQL, lineId, stationId, distance);

        return jdbcTemplate.queryForObject(SQL, (rs, rowNum)
                -> new Section(
                rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("station_id"),
                rs.getInt("distance")
        ), stationId, lineId);
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
        return jdbcTemplate.query(
                SQL,
                (resultSet, rowNo) -> new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("station_id"),
                        resultSet.getLong("line_id"),
                        resultSet.getInt("distance")
                ), lineId);
    }


    public void delete(long lineId, long stationId) {
        String SQL = "DELETE FROM section WHERE line_id = ? AND station_id = ?";
        jdbcTemplate.update(SQL,lineId,stationId);
    }
}
