package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_STATION = "insert into station (name) values (?)";
    private final String SELECT_STATION_BY_ID = "select * from station where id=?";
    private final String SELECT_STATION_BY_NAME = "Select * from station where name=?";
    private final String COUNT_STATION_BY_NAME = "Select count(*) From station where name=?";
    private final String SELECT_ALL = "select * from station";
    private final String DELETE_BY_ID = "delete from station where id=?";

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public int save(Station station) {
        return jdbcTemplate.update(INSERT_STATION, station.getName());
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_STATION_BY_ID, stationRowMapper, id);
    }

    public Station findByName(String name) {
        return jdbcTemplate.queryForObject(SELECT_STATION_BY_NAME, stationRowMapper, name);
    }

    public boolean hasSameStationName(String name) {
        int cnt = jdbcTemplate.queryForObject(COUNT_STATION_BY_NAME, Integer.class, name);
        return cnt != 0;
    }


    public List<Station> findAll() {
        return jdbcTemplate.query(SELECT_ALL, stationRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }


}
