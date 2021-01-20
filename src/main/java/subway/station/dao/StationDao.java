package subway.station.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

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

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(StationSql.INSERT.getSql(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Station.of(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(StationSql.SELECT.getSql(), actorRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(StationSql.DELETE_BY_ID.getSql(), id);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(StationSql.SELECT_BY_ID.getSql(), actorRowMapper, id);
    }
}
