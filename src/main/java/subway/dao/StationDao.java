package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.station.Station;

import java.util.List;

@Repository
public class StationDao {
    private final String STATION_INSERT_SQL = "insert into station (name) values (?)";
    private final String STATION_SELECT_BY_NAME = "select id, name from station where name = ?";
    private final String STATION_SELECT_BY_ID = "select id, name from station where id = ?";
    private final String STATION_SELECT_ALL = "select id, name from station";
    private final String STATION_DELETE_BY_ID = "delete from station where id = ?";
    private final String STATION_SELECT_COUNT_ID = "select count(id) from station where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationMapper = (rs, rowNum) ->
            new Station(rs.getLong(1), rs.getString(2));

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        this.jdbcTemplate.update(STATION_INSERT_SQL, station.getName());
        return this.jdbcTemplate.queryForObject(STATION_SELECT_BY_NAME, stationMapper, station.getName());
    }

    public Station getById(Long id) {
        return this.jdbcTemplate.queryForObject(STATION_SELECT_BY_ID, stationMapper, id);
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(STATION_SELECT_ALL, stationMapper);
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update(STATION_DELETE_BY_ID, id) > 0;
    }

    public boolean contain(Long id) {
        return this.jdbcTemplate.queryForObject(STATION_SELECT_COUNT_ID, Integer.class, id) > 0;
    }
}
