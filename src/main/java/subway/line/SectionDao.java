package subway.line;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SectionDao {

    private Long seq = 0L;
    private List<Section> sections = new ArrayList<>();


    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }

    public List<Section> findAll() {
        return sections;
    }

    private Section createNewObject(Section section) {
        Field idField = ReflectionUtils.findField(Section.class, "id");
        idField.setAccessible(true);
        ReflectionUtils.setField(idField, section, ++seq);
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

    public void update(Long id, Section updateSection) {
        Section section = findById(id).get();
        section.update(updateSection);
    }

    public List<Section> findByStationIdAndLineId(Long stationId, Long lineId) {
        return sections.stream()
                .filter(section -> isSameStationIdAndLineId(section,stationId,lineId))
                .collect(Collectors.toList());
    }

    private boolean isSameStationIdAndLineId(Section section, Long stationId, Long lineId) {
        return section.getLineId() == lineId &&
                (section.getUpStationId() == stationId || section.getDownStationId() == stationId);
    }
}
