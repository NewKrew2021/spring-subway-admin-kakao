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

    //TODO 가볍게 변경
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
        if (!sections.isCanSaveSection(section)) {
            throw new IllegalStationException();
        }
        save(section);
        if(sections.isExistUpStationAndMiddleSection(section)){
            addSectionBack(sections, section);
        }
        if(sections.isExistDownStationAndMiddleSection(section)){
            addSectionFront(sections, section);
        }
    }

    private void addSectionBack(Sections sections, Section section) {
        Section nextSection = sections.findSectionByUpStationId(section.getUpStationId());
        deleteSectionById(nextSection.getSectionId());
        save(new Section(section.getDownStationId(), nextSection.getDownStationId(), nextSection.getDistance() - section.getDistance(), section.getLineId()));
    }

    private void addSectionFront(Sections sections, Section section) {
        Section prevSection = sections.findSectionByDownStationId(section.getDownStationId());
        deleteSectionById(prevSection.getSectionId());
        save(new Section(prevSection.getUpStationId(), section.getUpStationId(), prevSection.getDistance() - section.getDistance(), section.getLineId()));
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
