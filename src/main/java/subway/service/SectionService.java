package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.OrderedSections;
import subway.domain.Section;
import subway.request.SectionRequest;
import subway.response.SectionResponse;

import java.util.List;

@FunctionalInterface
interface SectionToLongFunction {
    Long applyAsLong(Section section);
}

@Service
public class SectionService {
    private final SectionDao sectionDao;

    @Autowired
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse addSectionToLine(SectionRequest sectionRequest) {
        OrderedSections orderedSections = getOrderedSectionsByLineId(sectionRequest.getLineId());
        Section sectionToAdd = sectionRequest.getDomain();

        validateSectionAddRequest(orderedSections.getOrderedStationIds(), sectionRequest);

        // 노선의 양끝에 구간을 추가하거나, 중간에 구간을 추가하거나 둘 중 하나이다.
        if (orderedSections.isAddToEdgeCase(sectionToAdd)) {
            return addSectionToEdgeOfLine(sectionToAdd);
        }
        return addSectionToMiddleOfLine(sectionToAdd, orderedSections);
    }

    public void deleteStationFromLine(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.getByLineId(lineId);
        Section upsideSectionToDelete = getMatchSection(sections, Section::getDownStationId, stationId);
        Section downsideSectionToDelete = getMatchSection(sections, Section::getUpStationId, stationId);

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
        validateDistance(sectionToSplit.getDistance(), sectionToAdd.getDistance());

        // 기존의 구간을 지우고, 두 구간으로 나누어 저장해야 한다.
        Section newSection = sectionDao.save(sectionToAdd);
        sectionDao.save(getAnotherSection(sectionToSplit, sectionToAdd));
        sectionDao.deleteById(sectionToSplit.getId());

        return new SectionResponse(newSection);
    }

    private static Section getAnotherSection(Section sectionToSplit, Section sectionToAdd) {
        // 하행 기준 분리 ex, (A - C) 에 (B - C) 추가
        if (sectionToSplit.getDownStationId().equals(sectionToAdd.getDownStationId())) {
            return new Section(sectionToSplit.getLineId(), sectionToSplit.getUpStationId(),
                    sectionToAdd.getUpStationId(), sectionToSplit.getDistance() - sectionToAdd.getDistance());
        }
        // 상행 기준 분리 ex, (A - C) 에 (A - B) 추가
        return new Section(sectionToSplit.getLineId(), sectionToAdd.getDownStationId(),
                sectionToSplit.getDownStationId(), sectionToSplit.getDistance() - sectionToAdd.getDistance());
    }

    private static void validateSectionAddRequest(List<Long> stationIds, SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException();
        }

        int containedNumber = (int) stationIds.stream()
                .filter(stationId -> stationId.equals(sectionRequest.getDownStationId()) ||
                        stationId.equals(sectionRequest.getUpStationId()))
                .count();

        if (containedNumber != 1) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateDistance(int distance, int requestedDistance) {
        if (distance <= requestedDistance) {
            throw new IllegalArgumentException();
        }
    }

    private static Section getMatchSection(List<Section> sections, SectionToLongFunction func, Long stationId) {
        return sections.stream()
                .filter(section -> func.applyAsLong(section).equals(stationId))
                .findFirst()
                .orElse(null);
    }

    private void deleteSectionFromEdgeOfLine(Section upsideSection, Section downsideSection) {
        Section toDeleteSection = downsideSection == null ? upsideSection : downsideSection;
        sectionDao.deleteById(toDeleteSection.getId());
    }

    private void deleteSectionFromMiddleOfLine(Section upsideSection, Section downsideSection) {
        Section newSection = mergedSection(upsideSection, downsideSection);
        // 두 구간 사이의 역이 없어졌으므로, 두 구간을 합친다.
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.deleteById(upsideSection.getId());
        sectionDao.save(newSection);
    }

    private static Section mergedSection(Section upsideSection, Section downsideSection) {
        return new Section(upsideSection.getLineId(), upsideSection.getUpStationId(),
                downsideSection.getDownStationId(),
                downsideSection.getDistance() + upsideSection.getDistance());
    }

    private OrderedSections getOrderedSectionsByLineId(Long id) {
        return new OrderedSections(sectionDao.getByLineId(id));
    }
}
