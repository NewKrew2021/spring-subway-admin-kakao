package subway.line.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import subway.line.entity.Line;
import subway.line.entity.Lines;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class LineDaoImpl implements LineDao {
    private static final String INSERT_QUERY = "insert into line(name, color) values(?, ?)";
    private static final String SELECT_ALL_QUERY = "select * from line";
    private static final String SELECT_BY_ID_QUERY = "select * from line where id = ?";
    private static final String UPDATE_QUERY = "update line set name = ?, color = ? where id = ?";
    private static final String DELETE_QUERY = "delete from line where id = ?";
    private static final RowMapper<Line> lineRowMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    private final JdbcTemplate jdbcTemplate;

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line insert(String name, String color) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = generatePreparedStatementCreator(name, color);
        jdbcTemplate.update(psc, keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Line(id, name, color);
    }

    private PreparedStatementCreator generatePreparedStatementCreator(String name, String color) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        };
    }

    @Override
    public Optional<Line> findLineById(Long id) {
//        Line line = null;
//        try {
//            line = jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, lineRowMapper, id);
//        } catch (EmptyResultDataAccessException e) {
//            return Optional.empty();
//        }
//        return Optional.ofNullable(line);
        try {
            Line line = jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, lineRowMapper, id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Lines> findAllLines() {
        try {
            List<Line> lineList = jdbcTemplate.query(SELECT_ALL_QUERY, lineRowMapper);
            Lines lines = new Lines(lineList);
            return Optional.of(lines);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int update(Line line) {
        return jdbcTemplate.update(UPDATE_QUERY,
                line.getName(),
                line.getColor(),
                line.getId()
        );
    }

    @Override
    public int delete(Long id) {
        return jdbcTemplate.update(DELETE_QUERY, id);
    }
}
