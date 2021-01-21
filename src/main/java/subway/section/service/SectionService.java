package subway.section.service;

import org.springframework.stereotype.Service;
import subway.section.domain.Section;
import subway.section.domain.SectionDao;
import subway.section.domain.Sections;
import subway.section.domain.strategy.SectionGenerateStrategy;
import subway.section.presentation.SectionRequest;
import subway.station.presentation.StationResponse;
import subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private static final int INITIAL_DISTANCE = 0;

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();
        SectionGenerateStrategy sectionGenerateStrategy = sections.validateAndGenerateStrategy(upStationId, downStationId, distance);
        sectionDao.save(sectionGenerateStrategy.getNewSection());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.validateDeleteStation(stationId);
        sectionDao.deleteByLineIdAndStationId(new Section(lineId, stationId, INITIAL_DISTANCE));
    }

    public void lineInitialize(Section firstSection, Section secondSection) {
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);
    }

    public List<StationResponse> findSortedStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        return sections.getSections().stream()
                .map(section -> stationService.getStationResponseById(section.getStationId()))
                .collect(Collectors.toList());
    }

}
