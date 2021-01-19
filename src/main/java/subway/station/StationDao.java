package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class StationDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    StationMapper stationMapper;

    public Station save(Station station) {
        jdbcTemplate.update("insert into STATION (name) values (?)",station.getName());
        return station;
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationMapper);
    }

    public Station findById(Long id) {
        String sql = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql,stationMapper, id);
    }

    public Station findByName(String name) {
        String sql = "select * from STATION where name = ?";
        return jdbcTemplate.queryForObject(sql,stationMapper, name);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from STATION where id = ?",id);
    }

}
