package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.exceptions.EmptyLineException;
import subway.exception.exceptions.InvalidLineException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private static final String NOT_FOUND_LINE_MESSAGE = "노선을 찾을 수 없습니다.";
    private static final String EMPTY_LINE_MESSAGE = "라인 내에 구간이 존재하지 않습니다.";

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

    public long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?, ?, ?, ?)",
                    new String[] {"id"}
                    );
            pstmt.setString(1, line.getName());
            pstmt.setString(2, line.getColor());
            pstmt.setLong(3, line.getStartStationId());
            pstmt.setLong(4, line.getEndStationId());
            return pstmt;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int countByName(String lineName) {
        return jdbcTemplate.queryForObject("select count(*) from LINE where name = ?", Integer.class, lineName);
    }

    public Line findById(long id) {
        try {
            return jdbcTemplate.queryForObject("select * from LINE where id = ?", lineRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidLineException(NOT_FOUND_LINE_MESSAGE+" : "+e.getMessage());
        }
    }

    public List<Line> findAll() {
        try {
            return jdbcTemplate.query("select * from LINE limit 10", lineRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyLineException(EMPTY_LINE_MESSAGE+" : "+e.getMessage());
        }
    }

    public Line updateLine(long id, Line line) {
        jdbcTemplate.update(
                "update LINE set name = ?, color = ? where id = ?",
                line.getName(), line.getColor(), id
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
