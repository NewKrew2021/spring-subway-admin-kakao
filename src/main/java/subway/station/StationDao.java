package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateStationNameException;
import subway.exceptions.InvalidStationArgumentException;
import subway.section.Section;

import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {

    public static final String INSERT_STATION = "insert into STATION(name) VALUES (?)";
    public static final String SELECT_STATION_BY_ID = "select id, name from station where id = ?";
    public static final String SELECT_STATION_BY_NAME = "select id, name from STATION where name = ?";
    public static final String SELECT_ALL_STATIONS = "select id, name from station";
    public static final String DELETE_STATION_BY_ID = "delete from station where id = ?";

    private JdbcTemplate jdbcTemplate;

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

    public Optional<Station> save(Station station) {
        jdbcTemplate.update(INSERT_STATION, station.getName());
        return findByName(station);
    }

    public Optional<Station> findByName(Station station) {
        List<Station> result = jdbcTemplate.query(SELECT_STATION_BY_NAME, stationRowMapper, station.getName());
        return result.stream().findAny();
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(SELECT_ALL_STATIONS, stationRowMapper);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_STATION_BY_ID, Long.valueOf(id));
    }

    public Optional<Station> findById(Long id) {
        List<Station> result = jdbcTemplate.query(SELECT_STATION_BY_ID, stationRowMapper, id);
        return result.stream().findAny();
    }
}
