package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.InvalidLineArgumentException;

import java.util.List;

@Repository
public class LineDao {

    public static final String DUPLICATE_LINE_ERROR_MESSAGE = "중복된 이름의 노선입니다.";
    public static final String NO_MATCHING_LINE_ERROR_MESSAGE = "해당하는 노선이 존재하지 않습니다.";
    public static final String INSERT_LINE = "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?,?,?,?)";
    public static final String SELECT_LINE_BY_NAME = "select * from LINE where name = ?";
    public static final String SELECT_LINE_BY_ID = "select * from LINE where id = ?";
    public static final String SELECT_ALL_LINES = "select * from LINE";
    public static final String UPDATE_LINE_NAME_COLOR_BY_ID = "update LINE set name = ?, color = ? where id = ?";
    public static final String DELETE_LINE_BY_ID = "delete from line where id = ?";
    public static final String UPDATE_LINE_START_END_STATIONS_BY_ID = "update LINE set start_station_id = ?, end_station_id = ? where id = ?";
    public static final int NO_DELETED_ROW = 1;
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

    public Line save(Line line) {
        try {
            jdbcTemplate.update(INSERT_LINE, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId());
            return jdbcTemplate.queryForObject(SELECT_LINE_BY_NAME, lineRowMapper, line.getName());
        } catch (Exception e) {
            throw new DuplicateLineNameException(DUPLICATE_LINE_ERROR_MESSAGE);
        }
    }

    public Line findById(Long id) {
        Line line = jdbcTemplate.queryForObject(SELECT_LINE_BY_ID, lineRowMapper, id);
        if (line == null) {
            throw new InvalidLineArgumentException(NO_MATCHING_LINE_ERROR_MESSAGE);
        }
        return line;
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(SELECT_ALL_LINES, lineRowMapper);
    }

    public Line updateLineNameAndColor(Line line) {
        jdbcTemplate.update(UPDATE_LINE_NAME_COLOR_BY_ID, line.getName(), line.getColor(), line.getId());
        return findById(line.getId());
    }

    public Line updateLineStartEndStations(Line line) {
        jdbcTemplate.update(UPDATE_LINE_START_END_STATIONS_BY_ID, line.getStartStationId(), line.getEndStationId(), line.getId());
        return findById(line.getId());
    }

    public void deleteById(Long id) {
        int deletedRow = jdbcTemplate.update(DELETE_LINE_BY_ID, id);
        if(deletedRow < NO_DELETED_ROW) {
            throw new InvalidLineArgumentException(NO_MATCHING_LINE_ERROR_MESSAGE);
        }
    }
}
