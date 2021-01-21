package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Section;
import subway.dao.SectionDao;
import subway.domain.SectionGroup;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    @Autowired
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section createSectionOnLine(Long id, Long upStationId, Long downStationId, int distance) {
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(id));
        Section insertedSection = sections.insertSection(id, upStationId, downStationId, distance);
        Section dividedSection = sections.divideSection(insertedSection);

        sectionDao.save(insertedSection);
        sectionDao.update(dividedSection);

        return insertedSection;
    }

    @Transactional
    public void deleteStationOnLine(Long lineId, Long stationId) {
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(lineId));
        Section deletedSection = sections.deleteStation(stationId);
        Section combinedSection = sections.combineSection(deletedSection);

        sectionDao.deleteById(deletedSection.getId());
        sectionDao.update(combinedSection);
    }
}
