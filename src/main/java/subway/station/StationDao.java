package subway.station;

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
public class StationDao {
    private static final String FIND_ALL_SQL = "select id, name from station";
    private static final String DELETE_BY_ID_SQL = "delete from station where id = ?";
    private static final String FIND_BY_ID_SQL = "select id, name from station where id = ?";
    private static final String COUNT_BY_NAME_SQL = "select count(*) from station where name = ?";
    private static final String COUNT_BY_ID_SQL = "select count(*) from station where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> {
        return new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    };

    public Station save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, actorRowMapper);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    public Station findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, actorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(COUNT_BY_NAME_SQL, Integer.class, name);
    }

    public int countById(Long id) {
        return jdbcTemplate.queryForObject(COUNT_BY_ID_SQL, Integer.class, id);
    }
}
