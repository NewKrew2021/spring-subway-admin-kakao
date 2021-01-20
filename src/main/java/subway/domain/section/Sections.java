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
            newSection = addSectionWhenUpStationExist(upStationId, downStationId, distance);
        }

        if (downStationExist) {
            newSection = addSectionWhenDownStationExist(upStationId, downStationId, distance);
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

    private Section addSectionWhenUpStationExist(long upStationId, long downStationId, int distance) {
        Section upSection = findByStationId(upStationId);
        Section downSection = getDownSection(upSection);

        if (downSection != null && downSection.calculateDistance(upSection) <= distance) {
            throw new InvalidSectionException(INVALID_DISTANCE_SECTION_ADD_ERROR);
        }

        return new Section(downStationId, upSection.getDistance() + distance, upSection.getLineId());
    }

    private Section addSectionWhenDownStationExist(long upStationId, long downStationId, int distance) {
        Section downSection = findByStationId(downStationId);
        Section upSection = getUpSection(downSection);

        if (upSection != null && downSection.calculateDistance(upSection) <= distance) {
            throw new InvalidSectionException("새 구간의 길이는 기존에 존재하던 구간의 길이보다 크지 않아야 합니다.");
        }

        return new Section(upStationId, downSection.getDistance() - distance, downSection.getLineId());
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
