package subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import subway.station.entity.Station;
import subway.station.entity.Stations;

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
    public Station insert(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = generatePreparedStatementCreator(name);
        jdbcTemplate.update(psc, keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Station(id, name);
    }

    private PreparedStatementCreator generatePreparedStatementCreator(String name) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        };
    }

    @Override
    public Optional<Station> findStationById(Long id) {
        try {
            Station station = jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, stationRowMapper, id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Stations> findStationsByIds(List<Long> ids) {
        try {
            String joinedIds = getJoinedString(ids);
            String sql = String.format(SELECT_BY_IDS_QUERY, joinedIds);
            List<Station> stationList = jdbcTemplate.query(sql, stationRowMapper);
            Stations stations = new Stations(stationList);
            return Optional.of(stations);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getJoinedString(List<Long> ids) {
        return ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Override
    public Optional<Stations> findAllStations() {
        try {
            List<Station> stationList = jdbcTemplate.query(SELECT_ALL_QUERY, stationRowMapper);
            Stations stations = new Stations(stationList);
            return Optional.of(stations);
        } catch (Exception e) {
            return Optional.empty();
        }
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
