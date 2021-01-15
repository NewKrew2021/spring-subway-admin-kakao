package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.line.Line;

import java.sql.PreparedStatement;
import java.util.List;

public class StationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        Station persistStation = new Station(keyHolder.getKey().longValue(), station.getName());

        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public static Station findById(Long stationId) {
        return stations.stream()
                .filter(station -> station.getId() == stationId)
                .findFirst()
                .orElse(null);
    }
}
