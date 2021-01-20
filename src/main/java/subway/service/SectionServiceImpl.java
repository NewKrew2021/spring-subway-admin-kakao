package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.exception.AlreadyExistDataException;
import subway.exception.DeleteImpossibleException;
import subway.exception.IllegalStationException;

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
        if (sectionDao.existSection(section)) {
            throw new AlreadyExistDataException();
        }
        checkAndAddSection(section);
    }

    private void checkAndAddSection(Section section) {
        Sections sections = sectionDao.getSectionsByLineId(section.getLineId());
        Long existStationId = sections.findStationExist(section);
        if (existStationId == -1) {
            throw new IllegalStationException();
        }
        save(section);
        if (section.getUpStationId() == existStationId) {
            addSectionBack(sections, section);
        }
        if (section.getDownStationId() == existStationId) {
            addSectionFront(sections, section);
        }
    }

    private void addSectionBack(Sections sections, Section section) {
        Long existStationId = section.getUpStationId();
        Line line = lineDao.findOne(section.getLineId());
        Section nextSection = sections.findSectionByUpStationId(existStationId);
        if (existStationId == line.getDownStationId()) {
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), section.getDownStationId()));
            return;
        }
        deleteSectionById(nextSection.getSectionId());
        save(new Section(section.getDownStationId(), nextSection.getDownStationId(), nextSection.getDistance() - section.getDistance(), line.getId()));
    }

    private void addSectionFront(Sections sections, Section section) {
        Long existStationId = section.getDownStationId();
        Line line = lineDao.findOne(section.getLineId());
        Section prevSection = sections.findSectionByDownStationId(existStationId);
        if (existStationId == line.getUpStationId()) {
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), section.getUpStationId(), line.getDownStationId()));
            return;
        }
        deleteSectionById(prevSection.getSectionId());
        save(new Section(prevSection.getUpStationId(), section.getUpStationId(), prevSection.getDistance() - section.getDistance(), line.getId()));
    }

    @Override
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineDao.findOne(lineId);

        if (!sections.isPossibleToDelete() || sections.findSectionByStationId(stationId) == null) {
            throw new DeleteImpossibleException();
        }

        Section nextSection = sections.findSectionByUpStationId(stationId);
        Section prevSection = sections.findSectionByDownStationId(stationId);

        if (nextSection != null && prevSection != null) {
            deleteSectionById(nextSection.getSectionId());
            deleteSectionById(prevSection.getSectionId());
            save(new Section(prevSection.getUpStationId(), nextSection.getDownStationId(), prevSection.getDistance() + nextSection.getDistance(), lineId));
        } else if (nextSection != null) {
            deleteSectionById(nextSection.getSectionId());
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), nextSection.getDownStationId(), line.getDownStationId()));
        } else if (prevSection != null) {
            deleteSectionById(prevSection.getSectionId());
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), prevSection.getUpStationId()));
        }
    }

    @Override
    public void deleteSectionById(Long sectionId) {
        if (sectionDao.deleteSectionById(sectionId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    @Override
    public void deleteSectionByLineId(Long lineId) {
        sectionDao.deleteSectionByLineId(lineId);
    }
}
