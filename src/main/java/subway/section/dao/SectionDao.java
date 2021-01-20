package subway.section.dao;

import subway.section.vo.Section;
import subway.section.vo.Sections;

import java.util.Optional;

public interface SectionDao {
    Section insert(Section section);

    Optional<Section> findSectionById(Long id);

    Sections findSectionsByLineId(Long lineId);

    Sections findAllSections();

    int update(Section section);

    int delete(Long id);
}
