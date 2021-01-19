package subway.section;


public interface SectionService {
    Section save(Section section);

    Sections getSectionsByLineId(Long lineId);

    boolean deleteSectionById(Long lineId);

    boolean saveSection(Section section);

    boolean deleteSection(Long lineId, Long stationId);
}
