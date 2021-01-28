package subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.section.repository.SectionDao;
import subway.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {
    @Autowired
    private SectionDao sectionDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    private final String SELECT_ALL = "SELECT * FROM STATION";
    private final String SELECT_ALL_BY_ID = "SELECT * FROM STATION where id = ?";
    private final String DELETE_BY_ID = "delete from STATION where id = ?";

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert
                .withTableName("STATION")
                .usingGeneratedKeyColumns("ID");
    }

    public Station save(Station station) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", station.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        return findById(id.longValue());
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(SELECT_ALL,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public Station findById(Long stationId) {
        return this.jdbcTemplate.queryForObject(SELECT_ALL_BY_ID,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")),
                stationId);
    }

    public void deleteById(Long id) {
        if (sectionDao.findByStationId(id).size() > 0) {
            throw new BadRequestException();
        }
        jdbcTemplate.update(DELETE_BY_ID, id);
    }
}
