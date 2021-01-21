package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.SectionRequest;
import subway.dao.StationDao;

@Service
public class SectionService {

    private static final int MINIMUM_SECTION_COUNT = 3;
    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public void add(Long id, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(id));
        Section newSection = new Section(lineDao.findById(id),
                stationDao.findById(sectionRequest.getUpStationId()).get(),
                stationDao.findById(sectionRequest.getDownStationId()).get(),
                sectionRequest.getDistance());

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

        if(sectionsCount <= MINIMUM_SECTION_COUNT) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다");
        }

        Section front = sectionDao.findSectionByLineIdAndDownStationId(id, stationId);
        Section rear = sectionDao.findSectionByLineIdAndUpStationId(id, stationId);
        sectionDao.deleteById(front.getId());
        sectionDao.deleteById(rear.getId());

        int distance = front.getDistance() + rear.getDistance();

        sectionDao.save(new Section(
                lineDao.findById(id),
                front.getUpStation(),
                rear.getDownStation(),
                distance));
    }

}
