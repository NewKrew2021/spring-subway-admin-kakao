package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.station.Station;

import java.util.List;

@Repository
public class StationDao {
    private final String STATION_SELECT_SQL = "select id, name from station ";
    private final String STATION_SELECT_BY_ID_SQL = STATION_SELECT_SQL + "where id = ?";
    private final String STATION_DELETE_BY_ID_SQL = "delete from station where id = ?";
    private final String STATION_SELECT_COUNT_ID_SQL = "select count(id) from station where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Station> stationMapper = (rs, rowNum) ->
            new Station(rs.getLong(1), rs.getString(2));

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public Station getById(Long id) {
        return this.jdbcTemplate.queryForObject(STATION_SELECT_BY_ID_SQL, stationMapper, id);
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(STATION_SELECT_SQL, stationMapper);
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update(STATION_DELETE_BY_ID_SQL, id) > 0;
    }

    public boolean contain(Long id) {
        return this.jdbcTemplate.queryForObject(STATION_SELECT_COUNT_ID_SQL, Integer.class, id) > 0;
    }
}
