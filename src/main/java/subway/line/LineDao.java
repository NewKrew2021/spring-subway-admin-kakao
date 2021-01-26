package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.exceptions.EmptyLineException;
import subway.section.Section;
import subway.section.Sections;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class LineDao {

    private static final String NOT_FOUND_LINE_MESSAGE = "노선을 찾을 수 없습니다.";

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into LINE(name, color) VALUES(?, ?)",
                    new String[] {"id"}
            );
            pstmt.setString(1, line.getName());
            pstmt.setString(2, line.getColor());
            return pstmt;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean checkExistByName(String lineName) {
        return jdbcTemplate.queryForObject(
                "select exists (select * from LINE where name = ?) as success", Boolean.class, lineName);
    }

    public Line findById(long id) {
        List<Map<String, Object>> line = jdbcTemplate.queryForList(
                "select L.id as line_id, L.name as line_name, L.color as line_color, " +
                        "S.id as section_id, S.up_station_id, S.down_station_id, S.distance " +
                        "from LINE L left outer join SECTION S on L.id = S.line_id " +
                        "where L.id = ?", id
        );
        return mapToLine(line);
    }

    public List<Line> findAll() {
        List<Map<String, Object>> lines = jdbcTemplate.queryForList(
                "select L.id as line_id, L.name as line_name, L.color as line_color, " +
                        "S.id as section_id, S.up_station_id, S.down_station_id, S.distance " +
                        "from LINE L left outer join SECTION S on L.id = S.line_id"
        );
        Map<Long, List<Map<String, Object>>> lineById = lines.stream()
                .collect(Collectors.groupingBy(line -> (long)line.get("line_id")));
        return lineById.values().stream()
                .map(this::mapToLine)
                .collect(Collectors.toList());
    }

    private Line mapToLine(List<Map<String, Object>> line) {
        if (line.isEmpty()) {
            throw new EmptyLineException(NOT_FOUND_LINE_MESSAGE);
        }
        List<Section> sections = extractSections(line);

        return new Line(
                (long)line.get(0).get("line_id"),
                (String)line.get(0).get("line_name"),
                (String)line.get(0).get("line_color"),
                new Sections(sections)
        );
    }

    private List<Section> extractSections(List<Map<String, Object>> line) {
        return line.stream()
                .map(section -> new Section(
                            (long)section.get("section_id"),
                            (long)section.get("line_id"),
                            (long)section.get("up_station_id"),
                            (long)section.get("down_station_id"),
                            (int)section.get("distance"))
                )
                .collect(Collectors.toList());
    }

    public Line updateLine(long id, Line line) {
        jdbcTemplate.update(
                "update LINE set name = ?, color = ? where id = ?",
                line.getName(), line.getColor(), id
        );
        return findById(id);
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from LINE where id = ?", id);
    }
}
