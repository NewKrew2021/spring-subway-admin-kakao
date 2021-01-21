package subway.section;

import subway.exceptions.sectionExceptions.SectionDeleteException;
import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.exceptions.sectionExceptions.SectionNoStationException;
import subway.exceptions.sectionExceptions.SectionSameSectionException;

import java.util.List;
import java.util.stream.Collectors;

import static subway.section.RelativeDistance.downStationDistance;
import static subway.section.RelativeDistance.upStationDistance;

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

    public void validateSection(Long upStationId, Long downStationId, int distance) {
        boolean isUpStationExist = isStationIdExist(upStationId);
        boolean isDownStationExist = isStationIdExist(downStationId);

        validateSameSection(isUpStationExist, isDownStationExist);
        validateNoStations(isUpStationExist, isDownStationExist);
        validateDistance(distance);

        if (isDownStationExist) {
            validateMakeStation(downStationId, upStationDistance(distance));
        }
        if (isUpStationExist) {
            validateMakeStation(upStationId, downStationDistance(distance));
        }
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

    private void validateMakeStation(Long stationId, int distance) {
        RelativeDistance StationRelativeDistance = getRelativeDistanceByStationId(stationId);
        int upStationRelativeDistance = Integer.min(StationRelativeDistance.calculateRelativeDistance(distance)
                , StationRelativeDistance.getRelativeDistance());
        int downStationRelativeDistance = Integer.max(StationRelativeDistance.calculateRelativeDistance(distance)
                , StationRelativeDistance.getRelativeDistance());

        distanceValidate(upStationRelativeDistance, downStationRelativeDistance);
    }

    private RelativeDistance getRelativeDistanceByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getStationId().equals(stationId))
                .findFirst()
                .map(Section::getRelativeDistance).get();
    }

    private void distanceValidate(int upStationRelativeDistance, int downStationRelativeDistance) {
        if (areThereAnyStationsBetween(upStationRelativeDistance, downStationRelativeDistance)) {
            throw new SectionIllegalDistanceException();
        }
    }

    private boolean areThereAnyStationsBetween(int upStationRelativeDistance, int downStationRelativeDistance) {
        return sections.stream().map(Section::getRelativeDistance)
                .filter(relativeDistance ->
                        relativeDistance.isBetween(upStationRelativeDistance, downStationRelativeDistance))
                .count() != BASIC_NUM_OF_NEW_SECTION;
    }

    public int calculateRelativeDistance(Long upStationId, Long downStationId, int distance) {
        if (isStationIdExist(upStationId)) {
            return getRelativeDistanceByStationId(upStationId).calculateRelativeDistance(downStationDistance(distance));
        }
        return getRelativeDistanceByStationId(downStationId).calculateRelativeDistance(upStationDistance(distance));
    }

    public List<Long> getSortedStationIdsByDistance() {
        return sections.stream().sorted(Section::compareDistance)
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
