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

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
