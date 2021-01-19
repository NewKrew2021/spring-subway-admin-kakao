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
        return jdbcTemplate.queryForObject("select * from LINE where id = ?", lineRowMapper, id);
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from LINE limit 10", lineRowMapper);
    }

    public Line updateLine(long id, LineRequest lineRequest) {
        jdbcTemplate.update(
                "update LINE set name = ?, color = ? where id = ?",
                lineRequest.getName(), lineRequest.getColor(), id
        );
        return findById(id);
    }

    public Line updateLineStartStation(long lineId, long stationId) {
        jdbcTemplate.update("update LINE set start_station_id = ? where id = ?", stationId, lineId);
        return findById(lineId);
    }

    public Line updateLineEndStation(long lineId, long stationId) {
        jdbcTemplate.update("update LINE set end_station_id = ? where id = ?", stationId, lineId);
        return findById(lineId);
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from LINE where id = ?", id);
    }
}
