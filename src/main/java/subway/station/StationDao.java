package subway.station;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateStationException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

@Repository
public class StationDao {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert
                .withTableName("STATION")
                .usingGeneratedKeyColumns("ID");
    }

    public Station save(Station station) {
        try{
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", station.getName());
            Number id = simpleJdbcInsert.executeAndReturnKey(params);

            return findById(id.longValue());
        } catch (DataIntegrityViolationException e){
            throw new DuplicateStationException();
        }
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query("SELECT * FROM STATION",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public Station findById(Long stationId) {
        return this.jdbcTemplate.queryForObject("SELECT * FROM STATION where id = ?",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")),
                stationId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from STATION where id = ?", id);
    }
}
