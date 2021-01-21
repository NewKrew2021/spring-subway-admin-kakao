package subway.section;

import org.springframework.stereotype.Service;
import subway.distance.Distance;
import subway.line.Line;
import subway.line.LineRequest;

@Service
public class SectionService {

    public static final int MINIMUM_SECTIONS_COUNT = 3;

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void initialSave(Line line, LineRequest lineRequest) {
        sectionDao.save(new Section(line.getId(), Line.HEADID, lineRequest.getUpStationId(), Distance.VIRTUAL_DISTANCE));
        sectionDao.save(new Section(line.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        sectionDao.save(new Section(line.getId(), lineRequest.getDownStationId(), Line.TAILID, Distance.VIRTUAL_DISTANCE));
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    public boolean hasSectionOverlapped(Long lineId, SectionRequest sectionRequest) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        return sections.hasSameSection(new Section(lineId, sectionRequest));
    }
    
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        if (isStationExist(lineId, sectionRequest.getUpStationId())) {
            addSectionBasedUpStation(lineId, sectionRequest);
            return;
        }
        addSectionBasedDownStation(lineId, sectionRequest);
    }

    private boolean isStationExist(Long lineId, Long stationId) {
        return sectionDao.countByUpStationId(lineId, stationId);
    }

    private void addSectionBasedUpStation(Long lineId, SectionRequest sectionRequest) {
        Section front = sectionDao.findSectionByUpStationId(lineId, sectionRequest.getUpStationId());
        sectionDao.save(new Section(lineId, sectionRequest));
        sectionDao.save(new Section(lineId,
                sectionRequest.getDownStationId(),
                front.getDownStationId(),
                front.subtractDistance(sectionRequest)));
        sectionDao.delete(front.getId());
    }

    private void addSectionBasedDownStation(Long lineId, SectionRequest sectionRequest) {
        Section front = sectionDao.findSectionByDownStationId(lineId, sectionRequest.getDownStationId());
        sectionDao.save(new Section(lineId, sectionRequest));
        sectionDao.save(new Section(lineId,
                front.getUpStationId(),
                sectionRequest.getUpStationId(),
                front.subtractDistance(sectionRequest)));
        sectionDao.delete(front.getId());
    }

    public Sections findSectionsForDelete(Long lineId, Long stationId) {
        return sectionDao.findJointSections(lineId, stationId);
    }

    public boolean isDeletable(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId).size() > MINIMUM_SECTIONS_COUNT;
    }

    public NamedSections findNamedSectionsByLineId(Long lineId) {
        return sectionDao.findNamedSectionByLineId(lineId);
    }

    public void deleteSections(Sections sections) {
        sectionDao.deleteSections(sections);
    }
}
