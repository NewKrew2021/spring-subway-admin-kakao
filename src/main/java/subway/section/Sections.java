package subway.section;

import subway.exceptions.sectionExceptions.SectionDeleteException;
import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.exceptions.sectionExceptions.SectionNoStationException;
import subway.exceptions.sectionExceptions.SectionSameSectionException;
import subway.section.strategy.DownSectionGenerateStrategy;
import subway.section.strategy.SectionGenerateStrategy;
import subway.section.strategy.UpSectionGenerateStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private static final int NUM_OF_INITIAL_STATIONS = 2;
    private static final int BASIC_NUM_OF_NEW_SECTION = 1;
    public static final int MINIMUM_DISTANCE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Long getExtendedStationId(Long upStationId, Long downStationId) {
        if (isStationIdExist(upStationId)) {
            return downStationId;
        }
        return upStationId;
    }

    private boolean isStationIdExist(Long stationId) {
        return sections.stream()
                .map(Section::getStationId)
                .anyMatch(lineId -> lineId.equals(stationId));
    }

    public SectionGenerateStrategy determineStrategy(Long upStationId, Long downStationId, int distance) {
        if (isStationIdExist(upStationId)) {
            return new DownSectionGenerateStrategy().make(
                    getSectionByStationId(upStationId),
                    downStationId,
                    distance
            );
        }
        return new UpSectionGenerateStrategy().make(
                getSectionByStationId(downStationId),
                upStationId,
                distance
        );
    }

    private Section getSectionByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getStationId().equals(stationId))
                .findFirst()
                .get();
    }

    public SectionGenerateStrategy validateAndGenerateStrategy(Long upStationId, Long downStationId, int distance) {
        boolean isUpStationExist = isStationIdExist(upStationId);
        boolean isDownStationExist = isStationIdExist(downStationId);

        validateSameSection(isUpStationExist, isDownStationExist);
        validateNoStations(isUpStationExist, isDownStationExist);
        validateDistance(distance);

        SectionGenerateStrategy sectionGenerateStrategy = determineStrategy(upStationId, downStationId, distance);
        distanceValidate(sectionGenerateStrategy.getUpSection().getRelativePosition()
                , sectionGenerateStrategy.getDownSection().getRelativePosition());
        return sectionGenerateStrategy;
    }

    private void validateNoStations(boolean isUpStationExist, boolean isDownStationExist) {
        if (!isUpStationExist && !isDownStationExist) {
            throw new SectionNoStationException();
        }
    }

    private void validateSameSection(boolean isUpStationExist, boolean isDownStationExist) {
        if (isUpStationExist && isDownStationExist) {
            throw new SectionSameSectionException();
        }
    }

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new SectionIllegalDistanceException();
        }
    }

    private void distanceValidate(int upStationRelativePosition, int downStationRelativePosition) {
        if (areThereAnyStationsBetween(upStationRelativePosition, downStationRelativePosition)) {
            throw new SectionIllegalDistanceException();
        }
    }

    private boolean areThereAnyStationsBetween(int upStationRelativePosition, int downStationRelativePosition) {
        return sections.stream().map(Section::getRelativePosition)
                .filter(targetRelativePosition ->
                        targetRelativePosition >= upStationRelativePosition
                                && targetRelativePosition <= downStationRelativePosition)
                .count() != BASIC_NUM_OF_NEW_SECTION;
    }

    public List<Long> getSortedStationIds() {
        return sections.stream().sorted(Section::comparePosition)
                .map(Section::getStationId)
                .collect(Collectors.toList());
    }

    public void validateDeleteStation(Long stationId) {
        if (!isStationIdExist(stationId)) {
            throw new SectionDeleteException();
        }
        if (sections.size() == NUM_OF_INITIAL_STATIONS) {
            throw new SectionDeleteException();
        }
    }

}
