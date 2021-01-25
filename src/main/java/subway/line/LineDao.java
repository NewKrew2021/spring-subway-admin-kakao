package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {

    public static final String INSERT_LINE = "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?,?,?,?)";
    public static final String SELECT_LINE_BY_NAME = "select * from LINE where name = ?";
    public static final String SELECT_LINE_BY_ID = "select * from LINE where id = ?";
    public static final String SELECT_ALL_LINES = "select * from LINE";
    public static final String DELETE_LINE_BY_ID = "delete from line where id = ?";
    public static final String UPDATE_LINE_BY_ID = "update LINE set name = ?, color = ?, start_station_id = ?, end_station_id = ? where id = ?";
    private JdbcTemplate jdbcTemplate;

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

    public Optional<Line> save(Line line) {
        jdbcTemplate.update(INSERT_LINE, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId());
        List<Line> result = jdbcTemplate.query(SELECT_LINE_BY_NAME, lineRowMapper, line.getName());
        return result.stream().findAny();
    }

    public Optional<Line> findById(Long id) {
        List<Line> result = jdbcTemplate.query(SELECT_LINE_BY_ID, lineRowMapper, id);
        return result.stream().findAny();
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(SELECT_ALL_LINES, lineRowMapper);
    }

    public Optional<Line> updateById(Line line) {
        jdbcTemplate.update(UPDATE_LINE_BY_ID, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId(), line.getId());
        return findById(line.getId());
    }

    public int deleteById(Long id) {
        int deletedRow = jdbcTemplate.update(DELETE_LINE_BY_ID, id);
        return deletedRow;
    }
}
