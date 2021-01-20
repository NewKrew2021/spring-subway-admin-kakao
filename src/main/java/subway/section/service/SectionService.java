package subway.section.service;

import org.springframework.stereotype.Service;
import subway.line.domain.Line;
import subway.section.dao.SectionDao;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.section.dto.SectionRequest;

@Service
public class SectionService {

    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void add(Long id, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(id));
        Section newSection = new Section(id, sectionRequest);

        if(sections.hasSameSection(newSection)){
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }

        if(sections.isNotExistStations(newSection)){
            throw new IllegalArgumentException("요청한 역 중 하나는 노선에 존재해야 합니다.");
        }

        Section originSection = sections.sameUpStationOrDownStation(newSection);
        Section subSection = originSection.getSubSection(newSection);

        sectionDao.save(newSection);
        sectionDao.save(subSection);
        sectionDao.deleteById(originSection.getId());
    }

    public void delete(Long id, Long stationId) {
        int sectionsCount = sectionDao.countByLineId(id);

        if(sectionsCount <= 3) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다");
        }

        Section front = sectionDao.findSectionByLineIdAndDownStationId(id, stationId);
        Section rear = sectionDao.findSectionByLineIdAndUpStationId(id, stationId);
        sectionDao.deleteById(front.getId());
        sectionDao.deleteById(rear.getId());

        int distance = front.getDistance() + rear.getDistance();

        sectionDao.save(new Section(id,
                front.getUpStationId(),
                rear.getDownStationId(),
                distance));
    }

}
