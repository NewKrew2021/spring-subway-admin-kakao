package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> Station.of(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public Station save(Station station) throws DuplicateKeyException {
        String sql = "insert into STATION (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Station.of(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    public Station findById(Long id) throws EmptyResultDataAccessException {
        String sql = "select id, name from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
    }

    public void deleteById(Long id) throws EmptyResultDataAccessException {
        String sql = "delete from STATION where id = ?";

        if (jdbcTemplate.update(sql, id) == 0) {
            throw new EmptyResultDataAccessException("삭제하려는 station이 존재하지 않습니다.", 1);
        }
    }
}
