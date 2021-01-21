package subway.section.domain;

import subway.exceptions.sectionExceptions.SectionDeleteException;
import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.exceptions.sectionExceptions.SectionNoStationException;
import subway.exceptions.sectionExceptions.SectionSameSectionException;
import subway.section.domain.Section;
import subway.section.domain.strategy.DownSectionGenerateStrategy;
import subway.section.domain.strategy.SectionGenerateStrategy;
import subway.section.domain.strategy.UpSectionGenerateStrategy;

import java.util.List;

public class Sections {
    private static final int NUM_OF_INITIAL_STATIONS = 2;
    public static final int MINIMUM_DISTANCE = 1;
    public static final int FIRST_INDEX = 0;
    public static final int NEXT_DOWNSECTION_INDEX_ADD_NUM = 1;
    public static final int NEXT_UPSECTION_INDEX_ADD_NUM = -1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public SectionGenerateStrategy validateAndGenerateStrategy(Long upStationId, Long downStationId, int distance) {
        boolean isUpStationExist = isStationIdExist(upStationId);
        boolean isDownStationExist = isStationIdExist(downStationId);
        validateInput(isUpStationExist,isDownStationExist,distance);
        return determineStrategy(upStationId, downStationId, distance);
    }

    private boolean isStationIdExist(Long stationId) {
        return sections.stream()
                .map(Section::getStationId)
                .anyMatch(lineId -> lineId.equals(stationId));
    }

    public SectionGenerateStrategy determineStrategy(Long upStationId, Long downStationId, int distance) {
        if (isStationIdExist(upStationId)) {
            int sectionIndex = sections.indexOf(getSectionByStationId(upStationId));
            return new DownSectionGenerateStrategy().make(
                    getSectionByStationId(upStationId),
                    downStationId,
                    distance,
                    getRelativePositionByIndex(sectionIndex + NEXT_DOWNSECTION_INDEX_ADD_NUM)
            );
        }
        int sectionIndex = sections.indexOf(getSectionByStationId(downStationId));
        return new UpSectionGenerateStrategy().make(
                getSectionByStationId(downStationId),
                upStationId,
                distance,
                getRelativePositionByIndex(sectionIndex + NEXT_UPSECTION_INDEX_ADD_NUM)
        );
    }

    private Section getSectionByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getStationId().equals(stationId))
                .findFirst()
                .get();
    }

    private int getRelativePositionByIndex(int sectionIndex) {
        if(sectionIndex >= sections.size()) {
            return Integer.MAX_VALUE;
        }
        if(sectionIndex < FIRST_INDEX) {
            return Integer.MIN_VALUE;
        }
        return sections.get(sectionIndex).getRelativePosition();
    }

    private void validateInput(boolean isUpStationExist, boolean isDownStationExist, int distance) {
        if (!isUpStationExist && !isDownStationExist) {
            throw new SectionNoStationException();
        }
        if (isUpStationExist && isDownStationExist) {
            throw new SectionSameSectionException();
        }
        if (distance < MINIMUM_DISTANCE) {
            throw new SectionIllegalDistanceException();
        }
    }

    public void validateDeleteStation(Long stationId) {
        if (!isStationIdExist(stationId)) {
            throw new SectionDeleteException();
        }
        if (sections.size() == NUM_OF_INITIAL_STATIONS) {
            throw new SectionDeleteException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
