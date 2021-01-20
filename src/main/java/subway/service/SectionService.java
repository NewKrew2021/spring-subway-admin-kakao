package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.exception.section.SectionDeletionException;

import java.util.List;
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
        Station upStation = stationService.find(section.getUpStationId());
        Station downStation = stationService.find(section.getDownStationId());
        Section newSection = sectionDao.save(section);
        return new Section(newSection.getId(), section.getLineId(), upStation, downStation, section.getDistance());
    }

    @Transactional(readOnly = true)
    public Sections getSectionsByLineId(Long lindId) {
        List<Section> sectionList = sectionDao.getByLineId(lindId).stream()
                .map(section ->
                        new Section(section.getId(), section.getLineId(), stationService.find(section.getUpStationId()),
                                stationService.find(section.getDownStationId()), section.getDistance()))
                .collect(Collectors.toList());
        return new Sections(sectionList);
    }

    @Transactional
    public Section createSection(Section section) {
        Sections sections = getSectionsByLineId(section.getLineId());
        sections.validateSectionSplit(section);
        if(sections.checkSplit(section)) {
            Section sectionToSplit = sections.findSectionToSplit(section);
            Section splitedSection = sectionToSplit.split(section);
            sectionDao.deleteById(sectionToSplit.getId());
            save(splitedSection);
        }
        return save(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        sections.validateDeleteSection(stationId);

        Optional<Section> upsideSectionToDelete = sections.getSectionFromDownStationId(stationId);
        Optional<Section> downsideSectionToDelete = sections.getSectionFromUpStationId(stationId);
        if(checkDeleteSections(upsideSectionToDelete, downsideSectionToDelete)) {
            deleteBothSection(upsideSectionToDelete, downsideSectionToDelete);
            return;
        }
        deleteOneSection(upsideSectionToDelete, downsideSectionToDelete);
    }

    private boolean checkDeleteSections(Optional<Section> upsideSectionToDelete, Optional<Section> downsideSectionToDelete) {
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
