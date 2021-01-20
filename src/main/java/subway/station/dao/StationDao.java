package subway.station.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.sql.PreparedStatement;
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

    public Long save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(StationQuery.SAVE.getQuery(),
                    new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        },keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(StationQuery.FIND_ALL.getQuery(), stationMapper);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(StationQuery.FIND_BY_ID.getQuery(),
                stationMapper, id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(StationQuery.DELETE_BY_ID.getQuery(), id);
    }

}
