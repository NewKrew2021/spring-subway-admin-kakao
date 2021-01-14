package subway.section;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
