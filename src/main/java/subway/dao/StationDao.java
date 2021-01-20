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
        String sql = "insert into station (name) values (?)";
        return jdbcTemplate.update(sql, station.getName());
    }

    public Station findById(Long id) {
        String sql = "select * from station where id=?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public Station findByName(String name) {
        String sql = "Select * from station where name=?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, name);

    }

    public boolean hasSameStationName(String name) {
        int cnt = jdbcTemplate.queryForObject("Select count(*) From station where name=?", Integer.class, name);
        return cnt != 0;
    }


    public List<Station> findAll() {

        return jdbcTemplate.query("select * from station", stationRowMapper);

    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from station where id=?", id);
    }


}
