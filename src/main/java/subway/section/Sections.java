package subway.section;

import subway.exceptions.exception.SectionDeleteException;
import subway.exceptions.exception.SectionIllegalDistanceException;
import subway.exceptions.exception.SectionNoStationException;
import subway.exceptions.exception.SectionSameSectionException;

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

    public boolean isStationIdExist(Long stationId) {
        return sections.stream()
                .map(Section::getStationId)
                .anyMatch(lineId -> lineId.equals(stationId));
    }

    public void validateSection(Long upStationId, Long downStationId, int distance) {
        validateSameSection(upStationId, downStationId);
        validateNoStations(upStationId, downStationId);
        validateDistance(distance);
        if (validateMakeDownStation(upStationId, downStationId, distance)) return;
        validateMakeUpStation(upStationId, downStationId, distance);
    }

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new SectionIllegalDistanceException();
        }
    }

    private void validateMakeUpStation(Long upStationId, Long downStationId, int distance) {
        if (!isStationIdExist(upStationId) && isStationIdExist(downStationId)) {
            int downDistance = getSectionDistanceByStationId(downStationId);
            distanceValidate(downDistance - distance, downDistance);
            return;
        }
    }

    private boolean validateMakeDownStation(Long upStationId, Long downStationId, int distance) {
        if (isStationIdExist(upStationId) && !isStationIdExist(downStationId)) {
            int upDistance = getSectionDistanceByStationId(upStationId);
            distanceValidate(upDistance, upDistance + distance);
            return true;
        }
        return false;
    }

    private void validateNoStations(Long upStationId, Long downStationId) {
        if (!isStationIdExist(upStationId) && !isStationIdExist(downStationId)) {
            throw new SectionNoStationException();
        }
    }

    private void validateSameSection(Long upStationId, Long downStationId) {
        if (isStationIdExist(upStationId) && isStationIdExist(downStationId)) {
            throw new SectionSameSectionException();
        }
    }

    private int getSectionDistanceByStationId(Long stationId) {
        return sections.stream().filter(section -> section.getStationId().equals(stationId))
                .findFirst()
                .map(Section::getDistance)
                .hashCode();
    }

    private void distanceValidate(int upDistance, int downDistance) {
        if (areThereAnyStationsBetweenNewSections(upDistance, downDistance)) {
            throw new SectionIllegalDistanceException();
        }
    }

    private boolean areThereAnyStationsBetweenNewSections(int upDistance, int downDistance) {
        return sections.stream().map(Section::getDistance)
                .filter(distance -> (distance >= upDistance && distance <= downDistance))
                .count() != BASIC_NUM_OF_NEW_SECTION;
    }

    public int calculateRelativeDistance(Long upStationId, Long downStationId, int distance) {
        if (isStationIdExist(upStationId)) {
            return getSectionDistanceByStationId(upStationId) + distance;
        }
        return getSectionDistanceByStationId(downStationId) - distance;
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
