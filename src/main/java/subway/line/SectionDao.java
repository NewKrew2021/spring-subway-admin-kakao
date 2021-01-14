package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SectionDao {
    private final List<Section> sections = new ArrayList<>();
    private Long seq = 0L;

    public Section save(Section section) {
        Section newSection = createNewObject(section);
        sections.add(newSection);
        return newSection;
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    public List<Section> getByLineId(Long id) {
        return sections.stream()
                .filter(section -> section.getLine_id().equals(id))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        sections.removeIf(it -> it.getId().equals(id));
    }
}
