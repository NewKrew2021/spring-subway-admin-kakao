package subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import subway.station.vo.Station;
import subway.station.vo.Stations;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StationDaoImpl implements StationDao {
    private static final String INSERT_QUERY = "insert into station(name) values(?)";
    private static final String SELECT_ALL_QUERY = "select * from station";
    private static final String SELECT_BY_ID_QUERY = "select * from station where id = ?";
    private static final String SELECT_BY_IDS_QUERY = "select * from station where id in (%s)";
    private static final String UPDATE_QUERY = "update station set name = ? where id = ?";
    private static final String DELETE_QUERY = "delete from station where id = ?";
    private static final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    public StationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station insert(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    INSERT_QUERY,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Station(id, station);
    }

    @Override
    public Optional<Station> findStationById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, stationRowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Stations findStationsByIds(List<Long> ids) {
        return new Stations(
                jdbcTemplate.query(String.format(SELECT_BY_IDS_QUERY,
                        ids.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                        ), stationRowMapper
                )
        );
    }

    @Override
    public Stations findAllStations() {
        return new Stations(
                jdbcTemplate.query(SELECT_ALL_QUERY, stationRowMapper)
        );
    }

    @Override
    public int update(Station station) {
        return jdbcTemplate.update(UPDATE_QUERY, station.getName(), station.getId());
    }

    @Override
    public int delete(Long id) {
        return jdbcTemplate.update(DELETE_QUERY, id);
    }
}
