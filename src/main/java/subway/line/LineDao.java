package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.section.Section;
import subway.section.Sections;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class LineDao {

    private static final String FIND_ALL_SQL = "select id, name, color, start_station_id, end_station_id from line";
    private static final String FIND_BY_ID_SQL = "select L.id as id, L.name as name, L.color as color, " +
            "L.start_station_id as start_station_id, L.end_station_id as end_station_id, " +
            "S.id as section_id, S.up_station_id as section_up_station_id, S.down_station_id as section_down_station_id, " +
            "S.distance as distance, S.line_id as line_id " +
            "from LINE L " +
            "right join SECTION S on L.id = S.line_id " +
            "where L.id = ?";
    private static final String COUNT_BY_NAME_SQL = "select count(*) from line where name = ?";
    private static final String DELETE_BY_ID_SQL = "delete from line where id = ?";
    private static final String UPDATE_BY_ID_SQL = "update line set name = ?, color = ?, start_station_id = ?, end_station_id = ? where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Line> rowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getLong("start_station_id"),
            resultSet.getLong("end_station_id")
    );

    private final RowMapper<Map<String, Object>> rowMapperWithSections = (resultSet, rowNum) -> Map.of(
            "id", resultSet.getLong("id"),
            "name", resultSet.getString("name"),
            "color", resultSet.getString("color"),
            "start_station_id", resultSet.getLong("start_station_id"),
            "end_station_id", resultSet.getLong("end_station_id"),
            "section", new Section(
                    resultSet.getLong("section_id"),
                    resultSet.getLong("section_up_station_id"),
                    resultSet.getLong("section_down_station_id"),
                    resultSet.getInt("distance"),
                    resultSet.getLong("line_id")
            )
    );

    public Line save(Line line) {

        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId(), null);
    }

    public Line findById(Long id) {
        List<Map<String, Object>> queryResults = jdbcTemplate.query(FIND_BY_ID_SQL, rowMapperWithSections, id);
        if (queryResults.isEmpty()) {
            return null;
        }
        return mapLine(queryResults);
    }

    private Line mapLine(List<Map<String, Object>> queryResults) {
        List<Section> sections = queryResults.stream()
                .map(result -> (Section) result.get("section"))
                .collect(Collectors.toList());
        return new Line(
                (Long) queryResults.get(0).get("id"),
                (String) queryResults.get(0).get("name"),
                (String) queryResults.get(0).get("color"),
                (Long) queryResults.get(0).get("start_station_id"),
                (Long) queryResults.get(0).get("end_station_id"),
                Sections.of(sections, (Long) queryResults.get(0).get("start_station_id"))
        );
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(COUNT_BY_NAME_SQL, Integer.class, name);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    public int updateById(Long id, Line line) {
        return jdbcTemplate.update(UPDATE_BY_ID_SQL, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId(), id);
    }
}
