package subway.domain.section;

import subway.exception.InvalidSectionException;

import java.util.List;

public class Sections {
    static final String LAST_SECTION_DELETE_ERROR = "노선의 마지막 구간은 삭제할 수 없습니다.";
    static final String NO_SUCH_STATION_DELETE_ERROR = "삭제하고자 하는 역이 노선에 존재하지 않습니다.";
    static final String ALREADY_EXIST_SECTION_ADD_ERROR = "등록하려는 노선이 이미 존재합니다.";
    static final String NO_CONNECTED_SECTION_ADD_ERROR = "이어진 노선이 없습니다.";
    static final String INVALID_DISTANCE_SECTION_ADD_ERROR = "새 구간의 길이는 기존에 존재하던 구간의 길이보다 크지 않아야 합니다.";

    private final int MINIMUM_STATION_SIZE = 2;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void deletable(long stationId) {
        if (sections.size() <= MINIMUM_STATION_SIZE) {
            throw new InvalidSectionException(LAST_SECTION_DELETE_ERROR);
        }

        if (!exists(stationId)) {
            throw new InvalidSectionException(NO_SUCH_STATION_DELETE_ERROR);
        }
    }

    public Section addSection(long upStationId, long downStationId, int distance) {
        boolean upStationExist = exists(upStationId);
        boolean downStationExist = exists(downStationId);
        validateSection(upStationExist, downStationExist);

        Section newSection = null;
        if (upStationExist) {
            newSection = makeNewSection(upStationId, downStationId, distance);
        }

        if (downStationExist) {
            newSection = makeNewSection(downStationId, upStationId, -distance);
        }

        return newSection;
    }

    private void validateSection(boolean upStationExist, boolean downStationExist) {
        if (upStationExist && downStationExist) {
            throw new InvalidSectionException(ALREADY_EXIST_SECTION_ADD_ERROR);
        }
        if (!upStationExist && !downStationExist) {
            throw new InvalidSectionException(NO_CONNECTED_SECTION_ADD_ERROR);
        }
    }

    private Section makeNewSection(long existStationId, long newStation, int distance) {
        Section existSection = findByStationId(existStationId);
        Section oppositeSection = getOppositeSection(existSection, distance);

        if (oppositeSection != null && existSection.calculateDistance(oppositeSection) <= Math.abs(distance)) {
            throw new InvalidSectionException(INVALID_DISTANCE_SECTION_ADD_ERROR);
        }

        return new Section(newStation, existSection.getDistance() + distance, existSection.getLineId());
    }

    private Section getOppositeSection(Section section, int distance) {
        if (distance > 0) {
            return getDownSection(section);
        }
        return getUpSection(section);
    }


    private Section getUpSection(Section section) {
        int prevIdx = sections.indexOf(section) - 1;
        try {
            return sections.get(prevIdx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Section getDownSection(Section section) {
        int nextIdx = sections.indexOf(section) + 1;
        try {
            return sections.get(nextIdx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private boolean exists(long stationId) {
        return sections.stream().anyMatch(section -> section.getStationId() == stationId);
    }

    private Section findByStationId(long stationId) {
        return sections.stream()
                .filter(station -> station.getStationId() == stationId)
                .findFirst()
                .orElse(null);
    }


}
