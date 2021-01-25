package subway.station.dao;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import org.springframework.stereotype.Repository;
import subway.exceptions.lineExceptions.LineNotFoundException;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private static final String DELETE_FROM_STATION_WHERE_ID = "delete from station where id = ?";
    private static final String SELECT_FROM_STATION = "select * from station";
    private static final String SELECT_FROM_STATION_WHERE_NAME = "select count(*) from station where name = ?";
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

    public boolean isNameExist(String name) {
        return jdbcTemplate.queryForObject(SELECT_FROM_STATION_WHERE_NAME, int.class, name) != 0;
    }

    public Station findById(long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_STATION_WHERE_ID, stationMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException("해당 id의 이 존재하지 않습니다.");
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FROM_STATION_WHERE_ID, id);
    }

}

