package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SectionDao {
    private final String SECTION_SELECT_BY_LINE_ID = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
    private final String SECTION_DELETE_BY_ID = "delete from section where id = ?";


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Section> sectionMapper = (rs, rowNum) ->
            new Section(rs.getLong(1), rs.getLong(2), rs.getLong(3),
                    rs.getLong(4), rs.getInt(5));

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getLineId(), section.getUpStation(),
                section.getDownStation(), section.getDistance());
    }

    public List<Section> getByLineId(Long id) {
        return jdbcTemplate.query(SECTION_SELECT_BY_LINE_ID, sectionMapper, id);
    }

    public boolean deleteById(Long id) {
        return jdbcTemplate.update(SECTION_DELETE_BY_ID, id) > 0;
    }
}
