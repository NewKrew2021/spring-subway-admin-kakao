package subway.dao;

import subway.domain.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.query.Sql;

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
        this.jdbcTemplate.update(Sql.INSERT_STATION, station.getName());
        return this.jdbcTemplate.queryForObject(Sql.SELECT_STATION_WITH_NAME,
                stationMapper,
                station.getName());
    }

    public Station getById(Long stationId) {
        return this.jdbcTemplate.queryForObject(
                Sql.SELECT_STATION_WITH_ID,
                stationMapper,
                stationId);
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(
                Sql.SELECT_ALL_STATIONS,
                stationMapper
        );
    }

    public boolean deleteById(Long stationId) {
        return this.jdbcTemplate.update(Sql.DELETE_STATION_WITH_ID, stationId) > 0;
    }
}
