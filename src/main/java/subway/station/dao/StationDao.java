package subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station insert(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(StationDaoQuery.INSERT, new String[]{"id"});
            st.setString(1, name);
            return st;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), name);
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(StationDaoQuery.FIND_ALL, stationRowMapper);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(StationDaoQuery.FIND_BY_ID, stationRowMapper, id);
    }

    public boolean delete(Long id) {
        return jdbcTemplate.update(StationDaoQuery.DELETE, id) > 0;
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(StationDaoQuery.COUNT_BY_NAME, int.class, name);
    }

    private final RowMapper<Station> stationRowMapper =
            (resultSet, rowNum) -> new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
}
