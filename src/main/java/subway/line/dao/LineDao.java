package subway.line.dao;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.line.query.LineQuery;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class LineDao {
    private JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Line save(Line line) {

        KeyHolder keyHoler = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(
                    LineQuery.INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());

            return preparedStatement;
        }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(
                LineQuery.SELECT_ALL,
                (resultSet,rowNum)->
                    new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                    )
                );
    }


    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(
                LineQuery.SELECT_BY_ID,
                (resultSet,rowNum)->
                    new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                    )
                ,id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineQuery.DELETE_BY_ID, id);
    }


    public void modify(Long id, LineRequest lineRequest) {
        jdbcTemplate.update(LineQuery.UPDATE_BY_ID,
                lineRequest.getName(), lineRequest.getColor(), id);
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(LineQuery.COUNT_BY_NAME, Integer.class, name);
    }
}
