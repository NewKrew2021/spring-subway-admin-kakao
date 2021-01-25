package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exceptions.IllegalSectionSave;
import subway.exceptions.NotFoundException;
import subway.station.Station;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public void createSection(Long lineId, SectionDto sectionDto) {
        Section newSection = new Section(lineId, sectionDto);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionDao.findAllByLineId(lineId));

        sectionsInOneLine.validateSave(newSection);
        sectionDao.save(newSection);

        Section updatedSection = sectionsInOneLine.getSectionToBeUpdated(newSection);
        if(null != updatedSection){
            sectionDao.update(updatedSection);
        }
    }

    @Transactional
    public List<Station> findSortedStationsByLineId(Long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sections);

        return sectionsInOneLine.getSortedStations().stream()
                .map(stationId -> stationService.findById(stationId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStation(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sections);
        deleteableCheck(lineId, stationId, sectionsInOneLine.getStationList());

        List<Section> sectionsToDelete = sectionsInOneLine.getSectionsThatContain(stationId);
        sectionsToDelete.stream().forEach(section -> sectionDao.deleteById(section.getId()));

        /* 종점이 아닌 역을 지우는 경우 */
        if(2 == sectionsToDelete.size()) {
            SectionPair sectionPair = new SectionPair(sectionsToDelete.get(0), sectionsToDelete.get(1));
            sectionDao.save(sectionPair.merge());
        }
    }

    @Transactional
    private void deleteableCheck(Long lineId, Long stationId, List<Long> stationIds) {
        if(!stationIds.contains(stationId)) {
            throw new NotFoundException("삭제할 station이 존재하지 않습니다.");
        }

        /* 존재하는 section의 수가 1 이하일 경우 */
        if(sectionDao.findAllByLineId(lineId).size() <= 1) {
            throw new IllegalSectionSave(sectionDao.findAllByLineId(lineId).size() + " " + "해당 line에서 더 이상 station을 삭제할 수 없습니다.");
        }
    }
}
