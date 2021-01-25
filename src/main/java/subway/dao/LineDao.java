package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.AlreadyExistDataException;
import subway.exception.DataEmptyException;
import subway.exception.DeleteImpossibleException;
import subway.exception.UpdateImpossibleException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class LineDao {
    public static final String DELETE_FROM_LINE_WHERE_ID = "delete from LINE where id = ?";
    public static final String SELECT_FROM_LINE = "select * from LINE";
    public static final String SELECT_FROM_LINE_WHERE_ID = "select L.id as id, L.name as name, L.color as color, SE.id as section_id, SE.distance as distance,SE.up_station_id as up_station_id, SE.down_station_id as down_station_id, UST.name as uname, DST.name dname  from LINE L left join SECTION SE on SE.line_id = L.id left join STATION UST on SE.up_station_id = UST.id left join STATION DST on SE.down_station_id = DST.id where L.id= ? ";
    public static final String UPDATE_LINE_SET_COLOR_NAME_WHERE_ID = "update LINE set color = ?, name = ? where id = ?";
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("line")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        try {
            Long lineId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return new Line(lineId, line.getName(), line.getColor());
        } catch (RuntimeException e) {
            throw new AlreadyExistDataException();
        }

    }

    public void deleteById(Long lineId) {
        String sql = DELETE_FROM_LINE_WHERE_ID;
        if (jdbcTemplate.update(sql, lineId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    public List<Line> findAll() {
        String sql = SELECT_FROM_LINE;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
    }

    public Line findOne(Long lineId) {
        String sql = SELECT_FROM_LINE_WHERE_ID;
        return mapLine(jdbcTemplate.queryForList(sql, lineId));
    }

    public void update(Line line) {
        String sql = UPDATE_LINE_SET_COLOR_NAME_WHERE_ID;
        if (jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getId()) == 0) {
            throw new UpdateImpossibleException();
        }
    }

    private Line mapLine(List<Map<String, Object>> resultSet) {
        if (resultSet.size() < 1) {
            throw new DataEmptyException();
        }
        List<Section> sections = getSections(resultSet);
        List<Station> stations = getStations(resultSet);
        return new Line((Long) resultSet.get(0).get("id"), resultSet.get(0).get("name").toString(), resultSet.get(0).get("color").toString(), new Sections(sections, stations));
    }

    private List<Station> getStations(List<Map<String, Object>> resultSet) {
        List<Station> stations = new LinkedList<>();
        resultSet.stream().forEach(result -> {
            stations.add(new Station((Long) result.get("up_station_id"), result.get("uname").toString()));
            stations.add(new Station((Long) result.get("down_station_id"), result.get("dname").toString()));
        });
        return stations;
    }

    private List<Section> getSections(List<Map<String, Object>> resultSet) {
        List<Section> sections = resultSet.stream()
                .map(result -> new Section((Long) result.get("section_id"),
                        (Long) result.get("up_station_id"),
                        (Long) result.get("down_station_id"),
                        (Integer) result.get("distance"),
                        (Long) result.get("id")))
                .collect(Collectors.toList());
        return sections;
    }
}
