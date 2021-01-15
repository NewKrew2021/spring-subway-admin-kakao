package subway.dao;

import subway.domain.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.query.Sql;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Section> sectionMapper = (rs, rowNum) ->
            new Section(rs.getLong(1), rs.getLong(2), rs.getLong(3),
                    rs.getLong(4), rs.getInt(5));

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource){
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
    }

    public List<Section> getByLineId(Long lineId) {
        return jdbcTemplate.query(Sql.SELECT_SECTION_WITH_LINE_ID, sectionMapper, lineId);
    }

    public boolean deleteById(Long sectionId) {
        return jdbcTemplate.update(Sql.DELETE_SECTION_WITH_ID, sectionId) > 0;
    }
}
