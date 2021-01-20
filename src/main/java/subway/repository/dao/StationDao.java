package subway.repository.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static subway.repository.query.StationQuery.*;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        return insertAtDB(station);
    }

    public List<Station> findAll() {
        List<Station> stations = jdbcTemplate.query(selectAllQuery, new StationMapper());
        return stations;
    }

    public Optional<Station> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(selectByIdQuery, new StationMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(deleteByIdQuery, id);
    }

    public boolean hasDuplicateName(String name) {
        return jdbcTemplate.queryForObject(countByNameQuery, int.class, name) != 0;
    }

    private Station insertAtDB(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    insertQuery,
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

    private final static class StationMapper implements RowMapper<Station> {
        @Override
        public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String name = rs.getString("name");

            return new Station(id, name);
        }
    }
}
