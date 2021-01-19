package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?, ?, ?, ?)",
                    new String[] {"id"}
                    );
            pstmt.setString(1, lineRequest.getName());
            pstmt.setString(2, lineRequest.getColor());
            pstmt.setLong(3, lineRequest.getUpStationId());
            pstmt.setLong(4, lineRequest.getDownStationId());
            return pstmt;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int countByName(String lineName) {
        return jdbcTemplate.queryForObject("select count(*) from LINE where name = ?", Integer.class, lineName);
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
