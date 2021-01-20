package subway.station.domain;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StationDao {
    private static final RowMapper<Station> stationMappers = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertStation(station), keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Station(id, station.getName());
    }

    private PreparedStatementCreator insertStation(Station station) {
        return con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into station (name) values(?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setString(1, station.getName());
            return psmt;
        };
    }

    @Transactional(readOnly = true)
    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station", stationMappers);
    }

    @Transactional(readOnly = true)
    public Optional<Station> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from station where id = ?", stationMappers, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public boolean existsBy(String name) {
        return jdbcTemplate.queryForObject("select count(*) from station where name = ?", int.class, name) != 0;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from station where id = ?", id);
    }
}
