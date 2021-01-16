package subway.station;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateStationException;

import java.util.List;

@Repository
public class StationDao {
    private JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        try{
            String SQL = "insert into STATION (name) values (?)";
            int stationId = jdbcTemplate.update(SQL, new Object[]{station.getName()});

            return findById(Long.valueOf(stationId));
        } catch (DataIntegrityViolationException e){
            throw new DuplicateStationException();
        }
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query("SELECT * FROM STATION",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public Station findById(Long stationId) {
        return this.jdbcTemplate.queryForObject("SELECT * FROM STATION where id = ?",
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")),
                stationId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from STATION where id = ?", id);
    }
}
