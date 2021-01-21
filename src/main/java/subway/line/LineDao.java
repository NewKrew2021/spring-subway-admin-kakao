package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LineDao {

    private static final String FIND_ALL_SQL = "select id, name, color, start_station_id, end_station_id from line";
    private static final String FIND_BY_ID_SQL = "select id, name, color, start_station_id, end_station_id from line where id = ?";
    private static final String COUNT_BY_NAME_SQL = "select count(*) from line where name = ?";
    private static final String DELETE_BY_ID_SQL = "delete from line where id = ?";
    private static final String UPDATE_BY_ID_SQL = "update line set name = ?, color = ?, start_station_id = ?, end_station_id = ? where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Line> actorRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getLong("start_station_id"),
            resultSet.getLong("end_station_id")
    );

    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, actorRowMapper);
    }

    public Line findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, actorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(COUNT_BY_NAME_SQL, Integer.class, name);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    public int updateById(Long id, Line line) {
        return jdbcTemplate.update(UPDATE_BY_ID_SQL, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId(), id);
    }
}
