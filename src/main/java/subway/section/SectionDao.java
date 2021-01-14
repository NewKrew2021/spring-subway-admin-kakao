package subway.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SectionDao {
    private final List<Section> sections = new ArrayList<>();

    public void save(Section section) {
        sections.add(section);
    }

    public List<Section> findByLineId(Long lineId) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getLineId(), lineId))
                .collect(Collectors.toList());
    }

    public void delete(Section section) {
        sections.remove(section);
    }
}
