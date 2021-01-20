package subway.station;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.section.SectionDao;
import subway.exceptions.BadRequestException;

import java.util.List;
import java.util.ArrayList;

@Repository
public class StationDao {
    private static final String DUPLICATE_STATION_EXCEPTION = "지하철역의 이름은 중복될 수 없습니다.";
    private static final String USING_STATION = "해당 지하철역은 현재 노선이나 구간에 사용중입니다.";
    private SectionDao sectionDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate, SectionDao sectionDao) {
        this.sectionDao = sectionDao;
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

            return findById(id.longValue());
        } catch (DataIntegrityViolationException e){
            throw new BadRequestException(DUPLICATE_STATION_EXCEPTION);
        }
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query("SELECT id, name FROM STATION",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public Station findById(Long stationId) {
        return this.jdbcTemplate.queryForObject("SELECT id, name FROM STATION where id = ?",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")),
                stationId);
    }

    public void deleteById(Long id) {
        if (sectionDao.findByStationId(id).size() > 0 ){
            throw new BadRequestException(USING_STATION);
        }
        jdbcTemplate.update("DELETE FROM STATION WHERE id = ?", id);
    }

    public List<Station> findByUpDownId(Long upStationId, Long downStationId) {
        List<Station> stations = new ArrayList<>();
        stations.add(findById(upStationId));
        stations.add(findById(downStationId));

        return stations;

    }
}
