package subway.section;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SectionDao {
    private Long seq = 0L;
    private List<Section> sections = new ArrayList<>();

    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }

    public Section findByUpStationId(long id) {
        try {
            return sections.stream().filter(section -> section.getUpStationId().equals(id)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
//            throw new NotExistException("해당 구간이 존재하지 않습니다.");
        }
    }

    public Section findByDownStationId(long id) {
        try {
            return sections.stream().filter(section -> section.getDownStationId().equals(id)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public void updateSection(long id, Section section) {
        int index = -1;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getId().equals(id)) {
                index = i;
            }
        }
        if (index == -1) {
            return;
        }
        sections.set(index, new Section(id, section.getUpStationId(), section.getDownStationId(),section.getDistance(),section.getLineId()));
    }

    public void deleteById(long id) {
        sections.removeIf(section -> section.getId().equals(id));
    }
}
