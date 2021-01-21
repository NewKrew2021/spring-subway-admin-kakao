package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.section.SectionDao;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.exceptions.BadRequestException;

@Repository
public class LineDao {
    @Autowired
    SectionDao sectionDao;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> rowMapper = (rs, rowNum) -> {
        Line newLine = new Line(
               rs.getLong("id"),
               rs.getString("name"),
               rs.getString("color"),
               rs.getInt("extra_fare")
        );
        return newLine;
    };

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(LineRequest lineRequest) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", lineRequest.getName())
                .addValue("color", lineRequest.getColor())
                .addValue("extra_fare", lineRequest.getExtraFare());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        return findById(id.longValue());
    }

    public void update(Long lineId, LineRequest lineRequest){
        jdbcTemplate.update("update line set name = ?, color = ?, extra_fare = ? where id=?",
                        lineRequest.getName(),
                        lineRequest.getColor(),
                        lineRequest.getExtraFare(),
                        lineId
                        );
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from LINE",rowMapper);
    }

    public Line findById(Long id) {
        return this.jdbcTemplate.queryForObject(
                "SELECT * FROM LINE where id = ?",
                rowMapper,
                id);
    }

    public void deleteById(Long lineId) {
        if(sectionDao.findByLineId(lineId).size() > 1){
            throw new BadRequestException();
        }
        jdbcTemplate.update("delete from LINE where id = ?", lineId);
        sectionDao.deleteByLineId(lineId);
    }
}
