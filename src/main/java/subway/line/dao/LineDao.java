package subway.line.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final LineMapper lineMapper;

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate, LineMapper lineMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineMapper = lineMapper;
    }

    public Long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(LineQuery.SAVE.getQuery(), new String[]{"id"});

            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            preparedStatement.setLong(3, line.getUpStationId());
            preparedStatement.setLong(4, line.getDownStationId());
            preparedStatement.setInt(5, line.getDistance());

            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineQuery.FIND_ALL.getQuery(), lineMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineQuery.DELETE_BY_ID.getQuery(), id);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_ID.getQuery(), lineMapper, id);
    }

    public Line findByName(String name) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_NAME.getQuery(), lineMapper, name);
    }

    public void update(Line line) {
        jdbcTemplate.update(LineQuery.UPDATE.getQuery(),
                line.getUpStationId(), line.getDownStationId(),
                line.getDistance(), line.getId());
    }
}
