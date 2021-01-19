package subway.section;


public interface SectionService {
    Section save(Section section);

    Sections getSectionsByLineId(Long lineId);

    boolean deleteSectionById(Long lineId);

    boolean deleteSection(Long lineId, Long stationId);

    boolean saveSectionAsRequest(SectionRequest sectionRequest, Long lineId);
}
