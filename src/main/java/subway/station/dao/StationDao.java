package subway.station.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final StationMapper stationMapper;

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate, StationMapper stationMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationMapper = stationMapper;
    }

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
