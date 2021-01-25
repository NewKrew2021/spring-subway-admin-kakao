package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Sections;

@Service
public class SectionService {

    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void add(Long id, Section section) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(id));
        Section newSection = Section.of(
                lineDao.findById(id),
                stationDao.findById(section.getUpStation().getId()),
                stationDao.findById(section.getDownStation().getId()),
                section.getDistance()
        );

        if(sections.hasSameSection(newSection)){
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }

        if(sections.isNotExistStations(newSection)){
            throw new IllegalArgumentException("요청한 역 중 하나는 노선에 존재해야 합니다.");
        }

        sectionDao.save(newSection);

        if(sections.isExtendTerminal(newSection)) {
            return;
        }

        Section originSection = sections.sameUpStationOrDownStation(newSection);
        Section subSection = originSection.getSubSection(newSection);
        sectionDao.save(subSection);
        sectionDao.deleteById(originSection.getId());
    }

    @Transactional
    public void delete(Long id, Long stationId) {
        Sections sections = new Sections((sectionDao.findSectionsByLineId(id)));

        if(sections.canNotDelete()) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다");
        }

        sectionDao.deleteByStationId(id, stationId);

        if(sections.isTerminalStation(stationId)) {
            return;
        }

        Section front = sections.findFrontSection(stationId);
        Section rear = sections.findRearSection(stationId);

        sectionDao.save(Section.of(
                lineDao.findById(id),
                front.getUpStation(),
                rear.getDownStation(),
                front.getDistance() + rear.getDistance()
        ));
    }

}
