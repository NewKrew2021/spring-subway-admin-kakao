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

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
        );
        return line;
    };

    public void save(Line line) {
        String sql = "insert into line (name,color,up_station_id,down_station_id) values (?,?,?,?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId());
    }

    public Line findLineByName(String name) {
        String sql = "select * from line where name=?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, name);

    }

    public boolean isContainSameName(String name) {
        String sql = "select count(*) from line where name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != 0;
    }

    public Line findById(Long lineId) {
        String sql = "select * from line where id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, lineId);
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public void update(Line line) {
        String sql = "update line set name=?, color = ?, up_station_id = ?, down_station_id = ?  where id=?";
        jdbcTemplate.update(
                sql, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(), line.getId());
    }


    public void delete(Long id) {
        String sql = "delete from line where id=?";
        jdbcTemplate.update(sql, id);

    }
}
