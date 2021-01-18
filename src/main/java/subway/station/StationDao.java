package subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        if (isExist(station.getName())) {
            throw new IllegalStateException("이미 등록된 지하철역 입니다.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into station (name) values(?)",
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setString(1, station.getName());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Station(id, station.getName());
    }

    private boolean isExist(String name) {
        return jdbcTemplate.queryForObject("select count(*) from station where name = ?", int.class, name) != 0;
    }

    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station", new StationMapper());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from station where id = ?", id);
    }

    public Optional<Station> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from station where id = ?", new StationMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private final static class StationMapper implements RowMapper<Station> {
        @Override
        public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        }
    }
}
