package subway.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.domain.LineResponse;
import subway.section.domain.Section;
import subway.section.dao.SectionDao;
import subway.section.domain.SectionRequest;
import subway.section.domain.Sections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    private LineDao lineDao;
    private SectionDao sectionDao;

    @Autowired
    SectionService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse deleteSection(Long lineId, Long stationId) {
        Line line = Line.of(lineId, sectionDao.getSectionsByLineId(lineId));
        line.checkOneSection();

        List<Section> endPointSections = line.getEndPointSections(stationId);

        if (endPointSections.size() == 1) {
            sectionDao.deleteById(endPointSections.get(FIRST_INDEX).getId());
            return LineResponse.of(line);
        }

        mergeSection(stationId, endPointSections);
        return LineResponse.of(line);
    }

    private void mergeSection(Long id, List<Section> sections) {
        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Long downStationId = sections.get(FIRST_INDEX).getUpStationId().equals(id) ?
                sections.get(FIRST_INDEX).getDownStationId() : sections.get(SECOND_INDEX).getDownStationId();
        Long upStationId = sections.get(SECOND_INDEX).getDownStationId().equals(id) ?
                sections.get(SECOND_INDEX).getUpStationId() : sections.get(FIRST_INDEX).getUpStationId();

        sectionDao.deleteById(sections.get(FIRST_INDEX).getId());
        sectionDao.deleteById(sections.get(SECOND_INDEX).getId());
        sectionDao.save(Section.of(sections.get(FIRST_INDEX).getLineId(), upStationId, downStationId, distance));
    }

    @Transactional
    public void deleteSectionByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        return sectionDao.getSectionsByLineId(lineId);
    }

    @Transactional
    public void update(Long id, Section newSection) {
        sectionDao.update(id, newSection);
    }
}
