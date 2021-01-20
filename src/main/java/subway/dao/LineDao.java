package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.Line;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_LINE = "insert into line (name,color,up_station_id,down_station_id) values (?,?,?,?)";
    private final String SELECT_BY_NAME = "select * from line where name=?";
    private final String COUNT_BY_NAME = "select count(*) from line where name = ?";
    private final String SELECT_BY_ID = "select * from line where id = ?";
    private final String SELECT_ALL = "select * from line";
    private final String UPDATE_LINE = "update line set name=?, color = ?, up_station_id = ?, down_station_id = ?  where id=?";
    private final String DELETE_BY_ID = "delete from line where id=?";

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
        );
        return line;
    };

    public int save(Line line) {
        return jdbcTemplate.update(INSERT_LINE, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId());
    }

    public Line findByName(String name) {
        return jdbcTemplate.queryForObject(SELECT_BY_NAME, lineRowMapper, name);
    }

    public boolean isContainSameName(String name) {
        int count = jdbcTemplate.queryForObject(COUNT_BY_NAME, Integer.class, name);
        return count != 0;
    }

    public Line findById(Long lineId) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID, lineRowMapper, lineId);
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(SELECT_ALL, lineRowMapper);
    }

    public void update(Line line) {
        jdbcTemplate.update(
                UPDATE_LINE, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(), line.getId());
    }

    public void delete(Long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }
}
