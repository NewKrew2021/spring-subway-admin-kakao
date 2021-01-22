package subway.station.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.exceptions.BadRequestException;
import subway.station.domain.Station;

import java.util.List;
import java.util.ArrayList;

@Repository
public class StationDao {
    private static final String FIND_ALL_STATION_SQL = "SELECT id, name FROM STATION";
    private static final String FIND_STATION_SQL = "SELECT id, name FROM STATION where id = ?";
    private static final String DELETE_STATION_SQL = "DELETE FROM STATION WHERE id = ?";
    private static final String DUPLICATE_STATION_EXCEPTION = "지하철역의 이름은 중복될 수 없습니다.";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert
                .withTableName("STATION")
                .usingGeneratedKeyColumns("ID");
    }

    public Station save(Station station) {
        try{
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", station.getName());
            Number id = simpleJdbcInsert.executeAndReturnKey(params);

            return new Station(id.longValue(), station.getName());
        } catch (DataIntegrityViolationException e){
            throw new BadRequestException(DUPLICATE_STATION_EXCEPTION);
        }
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(FIND_ALL_STATION_SQL,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public Station findById(Long stationId) {
        return this.jdbcTemplate.queryForObject(FIND_STATION_SQL,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")),
                stationId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_STATION_SQL, id);
    }

    public List<Station> findByUpDownId(List<Long> stationIdGroup) {
        List<Station> stations = new ArrayList<>();
        for (Long stationId : stationIdGroup) {
            stations.add(findById(stationId));
        }

        return stations;
    }

}