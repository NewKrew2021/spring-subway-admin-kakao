package subway.section.service;

import subway.section.entity.Section;
import subway.section.entity.Sections;

public interface SectionService {
    Section create(Section section);

    void connect(Section section);

    Section findSectionById(Long id);

    Sections findSectionsByLineId(Long lineId);

    void update(Section section);

    void delete(Long id);

    void deleteByLineId(Long lineId);

    void deleteByLineIdAndStationId(Long lineId, Long stationId);
}
