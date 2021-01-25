package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationService stationService;

    @Autowired
    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public Section save(Section section) {
        return makeSectionIncludeStation(sectionDao.save(section));
    }

    private Section makeSectionIncludeStation(Section section) {
        return new Section(section.getId(), section.getLineId(),
                stationService.find(section.getUpStationId()), stationService.find(section.getDownStationId()), section.getDistance());
    }

    @Transactional(readOnly = true)
    public Sections getSectionsByLineId(Long lineId) {
        List<Section> sections = sectionDao.getByLineId(lineId);
        Map<Long, Station> stationMap = generateStationMapFromSections(sections);

        return new Sections(sections.stream()
                .map(section ->
                        new Section(section.getId(), section.getLineId(), stationMap.get(section.getUpStationId()),
                                stationMap.get(section.getDownStationId()), section.getDistance()))
                .collect(Collectors.toList()));
    }

    private Map<Long, Station> generateStationMapFromSections(List<Section> sections) {
        Map<Long, Station> stationMap = new HashMap<>();
        sections.stream()
                .flatMap(section -> section.getStations().stream())
                .map(Station::getId)
                .distinct()
                .forEach(stationId -> stationMap.put(stationId, stationService.find(stationId)));
        return stationMap;
    }

    @Transactional
    public Section createSection(Section section) {
        Sections sections = getSectionsByLineId(section.getLineId());
        sections.validateSectionSplit(section);
        sections.findSectionToSplit(section)
                .ifPresent(sectionToSplit -> {
                    sectionDao.deleteById(sectionToSplit.getId());
                    save(sectionToSplit.exclude(section));
                });
        return save(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        sections.validateDeleteSection(stationId);
        Sections sectionsForDelete = sections.findByStationId(stationId);
        sectionsForDelete.forEach(section -> sectionDao.deleteById(section.getId()));
        save(sectionsForDelete.connect());
    }
}
