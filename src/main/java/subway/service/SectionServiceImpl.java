package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionDao sectionDao;

    private final LineDao lineDao;

    public SectionServiceImpl(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Override
    public Section save(Section section) {
        return sectionDao.save(section);
    }

    @Override
    public Sections getSectionsByLineId(Long lineId) {
        return sectionDao.getSectionsByLineId(lineId);
    }

    @Override
    @Transactional
    public void saveSection(Section section) {
        Line line = lineDao.findOne(section.getLineId());
        line.addSection(section);
        sectionDao.deleteSectionByLineId(line.getId());
        sectionDao.saveSections(line.getSections());
    }

    @Override
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineDao.findOne(lineId);
        line.deleteStation(stationId);
        sectionDao.deleteSectionByLineId(line.getId());
        sectionDao.saveSections(line.getSections());
    }
}
