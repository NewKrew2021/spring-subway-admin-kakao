package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.List;

@Component
public class StationDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Resource
    StationMapper stationMapper;

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public Station save(Station station) {
//        if(ObjectUtils.isEmpty(findById(station.getId()))){
//            throw new IllegalArgumentException("역이 중복됩니다.");
//        }
        jdbcTemplate.update("insert into STATION (name) values (?)",station.getName());
        return station;
    }

    public List<Station> findAll() {
//        return jdbcTemplate.queryForList("select * from STATION",Station.class, stationMapper);
        String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Station findById(Long id) {
//        return jdbcTemplate.queryForObject("select * from STATION where id = ?",Station.class,id, stationMapper);
        String sql = "select id, name from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from STATION where id = ?",id);
    }

}
