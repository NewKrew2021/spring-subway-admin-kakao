package subway.station.domain;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private static final String DELETE_FROM_STATION_WHERE_ID = "delete from station where id = ?";
    private static final String SELECT_FROM_STATION = "select * from station";
    private static final String SELECT_FROM_STATION_WHERE_NAME = "select * from station where name = ?";
    private static final String SELECT_FROM_STATION_WHERE_ID = "select * from station where id = ?";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    private final static RowMapper<Station> stationMapper = ((rs, rowNum) ->
            new Station(rs.getLong("id"),
                    rs.getString("name")));

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name" , station.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        return new Station(id.longValue(),station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(SELECT_FROM_STATION, stationMapper);
    }

    public Station findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_STATION_WHERE_NAME, stationMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Station findById(long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_STATION_WHERE_ID, stationMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FROM_STATION_WHERE_ID, id);
    }

}

