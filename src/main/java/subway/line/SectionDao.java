package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SectionDao {
    public static final int FIRST_INDEX = 0;
    public static final int MIN_SECTION_SIZE = 1;
    public static final int SECOND_INDEX = 1;
    public static final int NOT_MATCH = 0;
    public static final int UNIQUE_MATCH = 1;

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public Section save(Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        Section persistSection = new Section(
                keyHolder.getKey().longValue(),
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());

        return persistSection;
    }

    @Transactional
    public void add(Section newSection) {
        checkSameSection(newSection);
        checkNoStation(newSection);

        if (isFirstSection(newSection) || isLastSection(newSection)) {
            save(newSection);
            return;
        }

        addInMiddle(newSection);
    }

    private void addInMiddle(Section newSection) {
        Section matchedUpSection = excuteQuery("select * from SECTION where up_station_id = ?",
                newSection.getUpStationId());
        if (matchedUpSection != null) {
            checkDistance(matchedUpSection.getDistance(), newSection.getDistance());
            save(newSection);
            update(matchedUpSection.getId(), new Section(
                    matchedUpSection.getId(),
                    matchedUpSection.getLineId(),
                    newSection.getDownStationId(),
                    matchedUpSection.getDownStationId(),
                    matchedUpSection.getDistance() - newSection.getDistance()));
            return;
        }

        Section matchedDownSection = excuteQuery("select * from SECTION where down_station_id = ?",
                newSection.getDownStationId());

        checkDistance(matchedDownSection.getDistance(), newSection.getDistance());
        save(newSection);
        update(matchedDownSection.getId(), new Section(
                matchedDownSection.getId(),
                matchedDownSection.getLineId(),
                matchedDownSection.getUpStationId(),
                newSection.getUpStationId(),
                matchedDownSection.getDistance() - newSection.getDistance()));
    }

    private void update(Long originSectionId, Section newSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance(), originSectionId);
    }

    private void checkDistance(int originDistance, int newDistance) {
        if (originDistance <= newDistance) {
            throw new IllegalArgumentException("새로 추가할 구간의 거리가 더 큽니다.");
        }
    }

    private Section excuteQuery(String sql, Long param) {
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, param);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isLastSection(Section newSection) {
        String sql1 = "select count(*) from SECTION where down_station_id = ? and line_id = ?";
        String sql2 = "select count(*) from SECTION where up_station_id = ? and line_id = ?";

        return jdbcTemplate.queryForObject(sql1, Integer.class, newSection.getUpStationId(), newSection.getLineId()) == UNIQUE_MATCH &&
                jdbcTemplate.queryForObject(sql2, Integer.class, newSection.getUpStationId(), newSection.getLineId()) == NOT_MATCH;
    }

    private boolean isFirstSection(Section newSection) {
        String sql1 = "select count(*) from SECTION where down_station_id = ? and line_id = ?";
        String sql2 = "select count(*) from SECTION where up_station_id = ? and line_id = ?";

        return jdbcTemplate.queryForObject(sql1, Integer.class, newSection.getDownStationId(), newSection.getLineId()) == NOT_MATCH &&
                jdbcTemplate.queryForObject(sql2, Integer.class, newSection.getDownStationId(), newSection.getLineId()) == UNIQUE_MATCH;
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        checkOneSection(lineId);

        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION " +
                "where (up_station_id = ? or down_station_id = ?) and line_id = ?";
        List<Section> sections = jdbcTemplate.query(sql, actorRowMapper, stationId, stationId, lineId);

        if (sections.size() == MIN_SECTION_SIZE) {
            removeEndPoint(sections.get(FIRST_INDEX));
            return;
        }

        mergeSection(stationId, sections);
    }

    private void mergeSection(Long id, List<Section> sections) {
        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Long downStationId = sections.get(FIRST_INDEX).getUpStationId() == id ?
                sections.get(FIRST_INDEX).getDownStationId() : sections.get(SECOND_INDEX).getDownStationId();
        Long upStationId = sections.get(SECOND_INDEX).getDownStationId() == id ?
                sections.get(SECOND_INDEX).getUpStationId() : sections.get(FIRST_INDEX).getUpStationId();

        jdbcTemplate.update("delete from SECTION where id = ?", sections.get(FIRST_INDEX).getId());
        jdbcTemplate.update("delete from SECTION where id = ?", sections.get(SECOND_INDEX).getId());
        save(new Section(sections.get(FIRST_INDEX).getLineId(), upStationId, downStationId, distance));
    }

    private void removeEndPoint(Section section) {
        String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, section.getId());
    }

    private void checkOneSection(Long lineId) {
        String sql = "select count(*) from SECTION where line_id = ?";
        if (jdbcTemplate.queryForObject(sql, Integer.class, lineId) == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("제거할 수 없습니다.");
        }
    }

    private void checkSameSection(Section newSection) {
        String sql = "select count(*) from SECTION where (up_station_id = :upStationId and down_station_id = :downStationId) or " +
                "(up_station_id = :downStationId and down_station_id = :upStationId)" +
                "and line_id = :lineId";

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(newSection);

        if (namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class) != NOT_MATCH) {
            throw new IllegalArgumentException("같은 구역이 이미 등록되어 있습니다.");
        }
    }

    private void checkNoStation(Section newSection) {
        String sql = "select count(*) from SECTION " +
                "where up_station_id in (:upStationId, :downStationId) " +
                "or down_station_id in (:upStationId, :downStationId)" +
                "and line_id = :lineId";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(newSection);

        if (namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class) == NOT_MATCH) {
            throw new IllegalArgumentException("노선과 연결할 수 있는 역이 없습니다.");
        }
    }

    public List<Long> getStationIds(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";

        List<Section> sections = jdbcTemplate.query(sql, actorRowMapper, lineId);


        List<Long> sectionIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        Section firstSection = sections.stream()
                .filter(section -> !sectionIds.contains(section.getUpStationId()))
                .findFirst()
                .orElse(null);

        Map<Long, Section> longToSection = new HashMap<>();
        for (Section section : sections) {
            longToSection.put(section.getUpStationId(), section);
        }

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstSection.getUpStationId());
        for (Section iter = firstSection; iter != null; iter = longToSection.get(iter.getDownStationId())) {
            stationIds.add(iter.getDownStationId());
        }

        return stationIds;
    }
}
