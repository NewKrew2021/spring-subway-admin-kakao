package subway.section.application;

import org.springframework.stereotype.Service;
import subway.section.dao.SectionDao;
import subway.section.domain.Section;
import subway.section.domain.SectionType;
import subway.section.domain.Sections;
import subway.section.dto.SectionRequest;
import subway.section.exception.LeastSizeException;
import subway.section.exception.NoStationException;
import subway.section.exception.SectionBetweenStationExistException;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void insertSection(SectionRequest sectionRequest, long lineId) {
        Sections sections = new Sections(sectionDao.getSections(lineId));
        Section standardSection = sections.findStandardSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        SectionType sectionType = standardSection.sectionConfirm(sectionRequest.getUpStationId());

        long stationId = sectionRequest.getInsertStationId(sectionType);
        int newPosition = standardSection.calculateSectionPosition(sectionType, sectionRequest.getDistance());

        if (sections.checkDistance(sectionType, standardSection.getPosition(), newPosition)) {
            throw new SectionBetweenStationExistException();
        }

        Section newSection = new Section(stationId, lineId, newPosition);
        sectionDao.save(newSection);
    }

    public void deleteSection(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.getSections(lineId));
        if (!sections.hasSection(stationId)) {
            throw new NoStationException();
        }
        if (sections.isLeastSizeSections()) {
            throw new LeastSizeException();
        }
        sectionDao.delete(lineId, stationId);
    }
}
