package subway.section.dao;

import subway.section.entity.Section;
import subway.section.entity.Sections;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section insert(Long lineId, Long upStationId, Long downStationId, int distance);

    Section insert(Section section);

    Optional<Section> findSectionById(Long id);

    Optional<Sections> findSectionsByLineId(Long lineId);

    int update(Section section);

    int delete(Long id);

    int delete(List<Long> ids);

    int deleteByLineId(Long lineId);
}
