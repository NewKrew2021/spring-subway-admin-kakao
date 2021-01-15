package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
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

    public Section save(Section section, Long priority) {
        Section persistSection = createNewObject(section,priority);
        sections.add(persistSection);
        return persistSection;
    }

    public List<Section> findAll() {
        return sections;
    }

    private Section createNewObject(Section section, Long priority) {
        Field idField = ReflectionUtils.findField(Section.class, "id");
        idField.setAccessible(true);
        ReflectionUtils.setField(idField, section, ++seq);
        Field priorityField = ReflectionUtils.findField(Section.class, "priority");
        priorityField.setAccessible(true);
        ReflectionUtils.setField(priorityField, section, priority);
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
                .sorted(Comparator.comparingLong(Section::getPriority))
                .collect(Collectors.toList());
    }

}
