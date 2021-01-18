package subway.section;

import java.util.List;

public interface SectionService {
    Section save(Section section);
    List<Section> getSectionsByLineId(Long lineId);
    boolean deleteSectionById(Long lineId);
    boolean saveSection(Long lineId, Section section);
    boolean deleteSection(Long lineId, Long stationId);
}
