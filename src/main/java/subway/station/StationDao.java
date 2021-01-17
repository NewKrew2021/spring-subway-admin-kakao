package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into station (name) values (?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();
            return new Station(id, station.getName());
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException("동일한 이름을 가진 지하철 역이 이미 존재합니다.");
        }
    }

    public Station findOne(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from station where id = ?", stationRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoContentException("해당 id를 갖는 지하철 역이 존재하지 않습니다.");
        }
    }

    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station", stationRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from station where id = ?", id);
    }

}
