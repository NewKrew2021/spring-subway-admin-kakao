package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line, LineRequest lineRequest) {
        List<Line> lines = findAll();
        if(lines.stream().anyMatch((Line lineSaved) ->
                lineSaved.getName().equals(lineRequest.getName()) &&
                lineSaved.getUpStationId(sectionDao) == lineRequest.getUpStationId() &&
                        lineSaved.getDownStationId(sectionDao) == lineRequest.getDownStationId()
        )){
            throw new BadRequestException();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor())
                .addValue("extra_fare", line.getExtraFare());
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
        return jdbcTemplate.query("select * from LINE",
                (rs, rowNum) -> {
                    Line newLine = new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color"),
                            rs.getInt("extra_fare")
                    );
                    return newLine;
                });
    }

    public Line findById(Long id) {
        return this.jdbcTemplate.queryForObject("SELECT * FROM LINE where id = ?",
                (rs, rowNum) -> { Line newLine = new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color"),
                            rs.getInt("extra_fare")
                    );
                return newLine;
                }, id);
    }

    public void deleteById(Long lineId) {
        if(sectionDao.findByLineId(lineId).size() > 1){
            throw new BadRequestException();
        }
        jdbcTemplate.update("delete from LINE where id = ?", lineId);
        sectionDao.deleteByLineId(lineId);
    }
}
