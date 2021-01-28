package subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;
import subway.section.domain.Section;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import subway.station.domain.Station;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final String SELECT_LINE_QUERY = "select L.id as line_id, " +
            "L.extra_fare as extra_fare, " +
            "L.color as color, " +
            "L.name as name," +
            "SECTION.id as section_id," +
            "SECTION.distance as section_distance," +
            "UPSTATION.id as up_station_id, " +
            "UPSTATION.name as up_station_name, " +
            "DOWNSTATION.id as down_station_id, " +
            "DOWNSTATION.name as down_station_name " +
            "from LINE L " +
            "left outer join SECTION on L.id = SECTION.line_id " +
            "left outer join STATION UPSTATION on SECTION.up_station_id = UPSTATION.id " +
            "left outer join STATION DOWNSTATION on SECTION.down_station_id = DOWNSTATION.id ";

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line newLine) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", newLine.getName())
                .addValue("color", newLine.getColor())
                .addValue("extra_fare", newLine.getExtraFare());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        return findById(id.longValue());
    }

    public void update(Long lineId, Line line) {
        jdbcTemplate.update("update line set name = ?, color = ?, extra_fare = ? where id=?",
                line.getName(),
                line.getColor(),
                line.getExtraFare(),
                lineId
        );
    }

    public List<Line> findAll() {
        String sql = SELECT_LINE_QUERY;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        Map<Long, List<Map<String, Object>>> sectionsByLineId = result.stream()
                .collect(Collectors.groupingBy(row -> (Long) row.get("line_id")));
        return sectionsByLineId.entrySet().stream()
                .map(entry -> mapLine(entry.getValue()))
                .collect(Collectors.toList());
    }

    private Line mapLine(List<Map<String, Object>> sectionRows) {
        Line line = new Line(
                (Long) sectionRows.get(0).get("line_id"),
                (String) sectionRows.get(0).get("name"),
                (String) sectionRows.get(0).get("color"),
                ((Long) sectionRows.get(0).get("extra_fare")).intValue(),
                extractSections(sectionRows),
                extractStations(sectionRows)
        );
        return line;
    }

    private List<Section> extractSections(List<Map<String, Object>> sections) {
        if (sections.isEmpty() || sections.get(0).get("section_id") == null) {
            return Collections.EMPTY_LIST;
        }

        return sections.stream()
                .map(row -> {
                    return new Section(
                            (Long) row.get("section_id"),
                            (Long) row.get("line_id"),
                            (Long) row.get("up_station_id"),
                            (Long) row.get("down_station_id"),
                            (Integer) row.get("section_distance")
                    );
                })
                .collect(Collectors.toList());
    }

    private List<Station> extractStations(List<Map<String, Object>> sections) {
        if(sections.isEmpty() || sections.get(0).get("section_id") == null){
            return Collections.EMPTY_LIST;
        }


        Set<Long> stationIdSet = new HashSet<>();
        List<Station> stations = new ArrayList<>();
        sections
                .forEach(row -> {
                    Long upStationId = (Long) row.get("up_station_id");
                    Long downStationId = (Long) row.get("down_station_id");
                    if (!stationIdSet.contains(upStationId)) {
                        stationIdSet.add(upStationId);
                        stations.add(new Station(
                                (Long) row.get("up_station_id"),
                                (String) row.get("up_station_name")
                        ));
                    }
                    if (!stationIdSet.contains(downStationId)) {
                        stationIdSet.add(downStationId);
                        stations.add(new Station(
                                (Long) row.get("down_station_id"),
                                (String) row.get("down_station_name")
                        ));
                    }
                });
        return stations;
    }

    public Line findById(Long id) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(SELECT_LINE_QUERY + " where L.id = ?", id);
        return mapLine(result);
    }

    public void deleteById(Long lineId) {
        jdbcTemplate.update("delete from LINE where id = ?", lineId);
    }
}
