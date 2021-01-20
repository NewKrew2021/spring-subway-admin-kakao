package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.section.SectionDao;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.exceptions.BadRequestException;

@Repository
public class LineDao {
    private static final String DUPLICATE_LINE_EXCEPTION = "중복된 노선은 추가할 수 없습니다.";
    private static final String MORE_TWO_SECTION_EXCEPTION = "2개 이상의 구간이 존재하고 있어 해당 노선을 삭제할 수 없습니다.";

    private SectionDao sectionDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate, SectionDao sectionDao) {
        this.sectionDao = sectionDao;
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line, LineRequest lineRequest) {
        List<Line> lines = findAll();

        if(lines.size() > 0 && checkDuplicationLine(lineRequest).size() > 0 ) {
            throw new BadRequestException(DUPLICATE_LINE_EXCEPTION);
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor())
                .addValue("extra_fare", line.getExtraFare());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);

        return new Line(id.longValue(), line.getName(), line.getColor(), line.getExtraFare());
    }

    private List<Line> checkDuplicationLine(LineRequest lineRequest){
        String sql = "SELECT line.id, line.name, section.up_station_id, section.down_station_id, section.distance  " +
                "FROM line INNER JOIN section ON line.id = section.line_id " +
                "WHERE name = ? and up_station_id = ? and down_station_id = ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> { Line line = new Line(
                        rs.getLong("id"));
                        return line;},
                lineRequest.getName(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
    }

    public void update(Long lineId, LineRequest lineRequest){
        jdbcTemplate.update("UPDATE line SET name = ?, color = ?, extra_fare = ? WHERE id=?",
                        lineRequest.getName(),
                        lineRequest.getColor(),
                        lineRequest.getExtraFare(),
                        lineId
                        );
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("SELECT id, name, extra_fare, color FROM LINE",
                (rs, rowNum) -> { Line newLine = new Line(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getInt("extra_fare"));
                    return newLine;
                });
    }

    public Line findById(Long id) {
        return this.jdbcTemplate.queryForObject("SELECT id, name, extra_fare, color FROM LINE where id = ?",
                (rs, rowNum) -> { Line newLine = new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color"),
                            rs.getInt("extra_fare"));
                    return newLine;
                }, id);
    }

    public void deleteById(Long lineId) {
        if(sectionDao.findByLineId(lineId).size() > 1){
            throw new BadRequestException(MORE_TWO_SECTION_EXCEPTION);
        }
        jdbcTemplate.update("DELETE FROM LINE WHERE id = ?", lineId);
        jdbcTemplate.update("delete from section where line_id = ?", lineId);

    }
}
