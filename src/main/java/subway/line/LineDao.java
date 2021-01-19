package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateLineNameException;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getLong("start_station_id"),
                resultSet.getLong("end_station_id")
        );
        return line;
    };

    public long save(LineRequest lineRequest) {
        String sql = "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?,?,?,?)";
        long lineId;
        try {
            jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
            lineId = jdbcTemplate.queryForObject("select id from LINE where name = ?", Long.class, lineRequest.getName());
        } catch (Exception e) {
            throw new DuplicateLineNameException("중복된 이름의 노선입니다.");
        }
        return lineId;
    }

    public Line findById(long id) {
        String sql = "select * from line where id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public List<Line> findAll() {
        String sql = "select * from line limit 10";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line updateLine(long id, LineRequest lineRequest) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), id);
        return findById(id);
    }

    public Line updateLineStartStation(long lineId, long stationId) {
        String sql = "update line set start_station_id = ? where id = ?";
        jdbcTemplate.update(sql, stationId, lineId);
        return findById(lineId);
    }

    public Line updateLineEndStation(long lineId, long stationId) {
        String sql = "update line set end_station_id = ? where id = ?";
        jdbcTemplate.update(sql, stationId, lineId);
        return findById(lineId);
    }

    public int deleteById(long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
