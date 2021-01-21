package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.exception.section.SectionDeletionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Map<Long, Station> stationMap = generateStationMapFromSections(new Sections(sections));

        return new Sections(sections.stream()
                .map(section ->
                        new Section(section.getId(), section.getLineId(), stationMap.get(section.getUpStationId()),
                                stationMap.get(section.getDownStationId()), section.getDistance()))
                .collect(Collectors.toList()));
    }

    private Map<Long, Station> generateStationMapFromSections(Sections sections) {
        Map<Long, Station> stationMap = new HashMap<>();
        sections.getAllStations().stream()
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
                    save(sectionToSplit.split(section));
                });
        return save(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        sections.validateDeleteSection(stationId);

        Optional<Section> upsideSectionToDelete = sections.getSectionFromDownStationId(stationId);
        Optional<Section> downsideSectionToDelete = sections.getSectionFromUpStationId(stationId);
        if(shouldDeleteBothSection(upsideSectionToDelete, downsideSectionToDelete)) {
            deleteBothSection(upsideSectionToDelete, downsideSectionToDelete);
            return;
        }
        deleteOneSection(upsideSectionToDelete, downsideSectionToDelete);
    }

    private boolean shouldDeleteBothSection(Optional<Section> upsideSectionToDelete, Optional<Section> downsideSectionToDelete) {
        return upsideSectionToDelete.isPresent() && downsideSectionToDelete.isPresent();
    }

    private void deleteBothSection(Optional<Section> upsideSectionToDelete, Optional<Section> downsideSectionToDelete) {
        Section upsideSection = upsideSectionToDelete.orElseThrow(SectionDeletionException::new);
        Section downsideSection = downsideSectionToDelete.orElseThrow(SectionDeletionException::new);
        Section newSection = upsideSection.attach(downsideSection);
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.deleteById(upsideSection.getId());
        save(newSection);
    }

    private void deleteOneSection(Optional<Section> upsideSectionToDelete, Optional<Section> downsideSectionToDelete) {
        Section deleteSection = upsideSectionToDelete.orElseGet(() -> downsideSectionToDelete.orElseThrow(SectionDeletionException::new));
        sectionDao.deleteById(deleteSection.getId());
    }
}
