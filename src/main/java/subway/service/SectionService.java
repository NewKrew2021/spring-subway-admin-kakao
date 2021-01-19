package subway.service;


import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.domain.Sections;

public interface SectionService {
    Section save(Section section);

    Sections getSectionsByLineId(Long lineId);

    boolean deleteSectionById(Long lineId);

    boolean deleteSection(Long lineId, Long stationId);

    boolean saveSectionAsRequest(SectionRequest sectionRequest, Long lineId);
}
