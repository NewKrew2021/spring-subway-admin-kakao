package subway.section.service;

import subway.section.entity.LineSections;
import subway.section.entity.Section;

public interface SectionService {
    Section create(Long lineId, Long upStationId, Long downStationId, int distance);

    void connect(Section section);

    Section getSectionById(Long id);

    LineSections getSectionsByLineId(Long lineId);

    void update(Section section);

    void delete(Long id);

    void deleteByLineId(Long lineId);

    void deleteByLineIdAndStationId(Long lineId, Long stationId);
}
