package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SectionDao {
    private static SectionDao sectionDao;
    private Long seq = 0L;
    private List<Section> sections = new LinkedList<>();

    JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init(){
        sections = new ArrayList<>();
        seq = 0L;
    }

    public Section save(Section section){
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }



    public Section createNewObject(Section section){
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }



}
