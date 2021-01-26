package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.dao.queries.stationQuery;
import subway.domain.station.Station;
import subway.exceptions.DuplicateException;

import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        if(isExist(station)) {
            throw new DuplicateException("동일한 이름을 가지는 station이 이미 존재합니다.");
        }
        jdbcTemplate.update(stationQuery.insert, station.getName());
        return jdbcTemplate.queryForObject(stationQuery.selectIdAndNameByName, stationRowMapper, station.getName());
    }

    private boolean isExist(Station station) {
        return 0 < jdbcTemplate.queryForObject(stationQuery.countByName, int.class, station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(stationQuery.selectIdAndNameOfAll, stationRowMapper);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(stationQuery.selectIdAndNameById, stationRowMapper, id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(stationQuery.deleteById, id);
    }
}