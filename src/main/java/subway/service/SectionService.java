package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.OrderedSections;
import subway.domain.Section;
import subway.domain.Sections;
import subway.request.SectionRequest;
import subway.response.SectionResponse;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    @Autowired
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse addSectionToLine(SectionRequest sectionRequest) {
        Sections sections = sectionDao.getByLineId(sectionRequest.getLineId());
        OrderedSections orderedSections = new OrderedSections(sections);
        Section sectionToAdd = sectionRequest.getDomain();

        orderedSections.validateSectionAddRequest(sectionToAdd);

        // 노선의 양끝에 구간을 추가하거나, 중간에 구간을 추가하거나 둘 중 하나이다.
        if (orderedSections.isAddToEdgeCase(sectionToAdd)) {
            return addSectionToEdgeOfLine(sectionToAdd);
        }
        return addSectionToMiddleOfLine(sectionToAdd, orderedSections);
    }

    public void deleteStationFromLine(Long lineId, Long stationId) {
        Sections sections = sectionDao.getByLineId(lineId);
        Section upsideSectionToDelete = sections.getDownMatchSection(stationId);
        Section downsideSectionToDelete = sections.getUpMatchSection(stationId);

        // 노선의 양끝 구간에 속하는 역을 제거하거나, 노선 중간 구간에 속하는 역을 지운거나 둘 중 하나이다.
        if (upsideSectionToDelete == null || downsideSectionToDelete == null) {
            deleteSectionFromEdgeOfLine(upsideSectionToDelete, downsideSectionToDelete);
            return;
        }
        deleteSectionFromMiddleOfLine(upsideSectionToDelete, downsideSectionToDelete);
    }

    private SectionResponse addSectionToEdgeOfLine(Section sectionToAdd) {
        Section newSection = sectionDao.save(sectionToAdd);
        return new SectionResponse(newSection);
    }

    private SectionResponse addSectionToMiddleOfLine(Section sectionToAdd, OrderedSections orderedSections) {
        Section sectionToSplit = orderedSections.findSectionToSplit(sectionToAdd);
        Section anotherSection = sectionToSplit.getAnotherSection(sectionToAdd);

        // 기존의 구간을 지우고, 두 구간으로 나누어 저장해야 한다.
        Section newSection = sectionDao.save(sectionToAdd);
        sectionDao.save(anotherSection);
        sectionDao.deleteById(sectionToSplit.getId());

        return new SectionResponse(newSection);
    }

    private void deleteSectionFromEdgeOfLine(Section upsideSection, Section downsideSection) {
        Section toDeleteSection = downsideSection == null ? upsideSection : downsideSection;
        sectionDao.deleteById(toDeleteSection.getId());
    }

    private void deleteSectionFromMiddleOfLine(Section upsideSection, Section downsideSection) {
        Section newSection = upsideSection.mergeSection(downsideSection);

        // 두 구간 사이의 역이 없어졌으므로, 두 구간을 합친다.
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.deleteById(upsideSection.getId());
        sectionDao.save(newSection);
    }
}
