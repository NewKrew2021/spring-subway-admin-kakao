package subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.DuplicateException;

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
        if(hasDuplicateName(station.getName())){
            throw new DuplicateException();
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

        return new Station(
                id,
                station.getName()
        );
    }

    public List<Station> findAll() {
        String sqlQuery = "select * from station";
        List<Station> stations = jdbcTemplate.query(sqlQuery, new StationMapper());
        return stations;
    }

    public Optional<Station> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from station where id = ?", new StationMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        String sqlQuery = "delete from station where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private boolean hasDuplicateName(String name){
        String query = "select count(*) from station where name = ?";
        int count = jdbcTemplate.queryForObject(query, int.class, name);
        if (count != 0) return true;
        return false;
    }

    private final static class StationMapper implements RowMapper<Station> {
        @Override
        public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String name = rs.getString("name");

            return new Station(id, name);
        }
    }
}
