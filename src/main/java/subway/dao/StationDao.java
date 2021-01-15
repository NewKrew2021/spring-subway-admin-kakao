package subway.dao;

import subway.domain.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationMapper = (rs, rowNum) ->
            new Station(rs.getLong(1), rs.getString(2));

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        this.jdbcTemplate.update("insert into station (name) values (?)", station.getName());
        return this.jdbcTemplate.queryForObject("select * from station where name = ?",
                stationMapper,
                station.getName());
    }

    public Station getById(Long id) {
        return this.jdbcTemplate.queryForObject(
                "select * from station where id = ?",
                stationMapper,
                id);
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(
                "select * from station",
                stationMapper
        );
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update("delete from station where id = ?", id) > 0;
    }
}
