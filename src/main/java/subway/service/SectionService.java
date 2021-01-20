package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private SectionDao sectionDao;
    private StationService stationService;

    @Autowired
    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section save(Section section) {
        Station upStation = stationService.find(section.getUpStationId());
        Station downStation = stationService.find(section.getDownStationId());
        Section newSection = sectionDao.save(section);
        return new Section(newSection.getId(), section.getLineId(), upStation, downStation, section.getDistance());
    }

    public Sections getSectionsByLineId(Long lindId) {
        List<Section> sectionList = sectionDao.getByLineId(lindId).stream()
                .map(section ->
                        new Section(section.getId(), section.getLineId(), stationService.find(section.getUpStationId()),
                                stationService.find(section.getDownStationId()), section.getDistance()))
                .collect(Collectors.toList());
        return new Sections(sectionList);
    }

    public Section createSection(Section section) {
        Sections sections = getSectionsByLineId(section.getLineId());
        sections.validateSectionRequest(section);
        if(sections.checkSplit(section)) {
            Section sectionToSplit = sections.findSectionToSplit(section);
            Section splitedSection = sectionToSplit.split(section);
            sectionDao.deleteById(sectionToSplit.getId());
            save(splitedSection);
        }
        return save(section);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        sections.validateDeleteSection(stationId);

        Section upsideSectionToDelete = sections.getSectionFromDownStationId(stationId);
        Section downsideSectionToDelete = sections.getSectionFromUpStationId(stationId);
        if (downsideSectionToDelete == null || upsideSectionToDelete == null) {
            Section deleteSection = (downsideSectionToDelete == null ? upsideSectionToDelete : downsideSectionToDelete);
            sectionDao.deleteById(deleteSection.getId());
            return;
        }

        Section newSection = upsideSectionToDelete.attach(downsideSectionToDelete);
        sectionDao.deleteById(downsideSectionToDelete.getId());
        sectionDao.deleteById(upsideSectionToDelete.getId());
        save(newSection);
    }
}
