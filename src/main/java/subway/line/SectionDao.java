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

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    public static final int FIRST_INDEX = 0;
    public static final int MIN_SECTION_SIZE = 1;
    public static final int SECOND_INDEX = 1;

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
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
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

    public void add(Section newSection) {
        checkSameSection(newSection);
        checkNoStation(newSection);
        checkOneSection();
    }

    public void removeSection(Long id) {
        checkOneSection();

        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where up_station_id = ? or down_station_id = ?";
        List<Section> sections = jdbcTemplate.query(sql, actorRowMapper, id, id);

        if (sections.size() == 1) {
            removeEndPoint(sections.get(FIRST_INDEX));
            return;
        }

        mergeSection(id, sections);
    }

    private void mergeSection(Long id, List<Section> sections) {
        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Long downStationId = sections.get(FIRST_INDEX).getUpStationId() == id ?
                sections.get(FIRST_INDEX).getDownStationId() : sections.get(SECOND_INDEX).getDownStationId();
        Long upStationId = sections.get(SECOND_INDEX).getDownStationId() == id ?
                sections.get(SECOND_INDEX).getUpStationId() : sections.get(FIRST_INDEX).getUpStationId();

        jdbcTemplate.update("delete from section where id = ?", sections.get(FIRST_INDEX).getId());
        jdbcTemplate.update("delete from section where id = ?", sections.get(SECOND_INDEX).getId());
        save(new Section(sections.get(FIRST_INDEX).getLineId(), upStationId, downStationId, distance));
    }

    private void removeEndPoint(Section section) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, section.getId());
    }

    private void checkOneSection() {
        String sql = "select count(*) from section";
        if (jdbcTemplate.queryForObject(sql, Integer.class) == 1) {
            throw new IllegalArgumentException("제거할 수 없습니다.");
        }
    }

    private void checkSameSection(Section newSection) {
        String sql = "select count(*) from section where (up_station_id = :upStationId and down_station_id = :downStationId) or " +
                    "(up_station_id = :downStationId and down_station_id = :upStationId)";

            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(newSection);

            if (namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class) != 0) {
                throw new IllegalArgumentException("같은 구역이 이미 등록되어 있습니다.");
        }
    }

    private void checkNoStation(Section newSection) {
        String sql = "select count(*) from section where up_station_id = :upStationId or down_station_id = :downStationId or " +
                "up_station_id = :downStationId or down_station_id = :upStationId";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(newSection);

        if (namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class) == 0) {
            throw new IllegalArgumentException("노선과 연결할 수 있는 역이 없습니다.");
        }
    }

    public List<Long> getStationIds(Long id) {
        return null;
    }
}
