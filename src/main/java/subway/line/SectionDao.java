package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SectionDao {
    private static SectionDao sectionDao = null;
    private Long seq = 0L;
    private List<Section> sections = new ArrayList<>();

    private SectionDao() {}

    public static SectionDao getInstance() {
        if (sectionDao == null) {
            sectionDao = new SectionDao();
        }

        return sectionDao;
    }

    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }

    public List<Section> findAll() {
        return sections;
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    public void deleteById(Long id) {
        sections.removeIf(it -> it.getId().equals(id));
    }

    public Optional<Section> findById(Long id) {
        return sections.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    public List<Section> findByLineId(Long lineId){
        return sections.stream()
                .filter(section -> section.getLineId() == lineId)
                .collect(Collectors.toList());
    }
}
