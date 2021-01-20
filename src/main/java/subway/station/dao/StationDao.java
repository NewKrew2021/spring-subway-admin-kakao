package subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class StationDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    StationMapper stationMapper;

    public Station save(Station station) {
        jdbcTemplate.update(StationQuery.SAVE.getQuery(), station.getName());
        return station;
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(StationQuery.FIND_ALL.getQuery(), stationMapper);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(StationQuery.FIND_BY_ID.getQuery(),
                stationMapper, id);
    }

    public Station findByName(String name) {
        return jdbcTemplate.queryForObject(StationQuery.FIND_BY_NAME.getQuery(),
                stationMapper, name);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(StationQuery.DELETE_BY_ID.getQuery(), id);
    }

}
