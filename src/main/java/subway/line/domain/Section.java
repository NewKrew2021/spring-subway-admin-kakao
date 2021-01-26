package subway.line.domain;

import subway.line.dto.SectionRequest;

import java.util.List;
import java.util.Objects;

public class Section {
    private static final String SAME_SECTION_MESSAGE = "같은 section이 추가될 수 없습니다.";
    private static final String STATION_NOT_EXIST_MESSAGE = "어떤 station도 없으면 추가할 수 없습니다.";
    private static final String DISTANCE_EXCEED_MESSAGE = "추가하려는 거리가 기존의 거리보다 클 수 없습니다.";
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getId() {
        return id;
    }

    public void checkValidInsert(List<Section> sections) {
        checkSameSection(sections);
        checkNotIncluded(sections);
    }

    private void checkSameSection(List<Section> sections) {
        if (sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(upStationId) && section.getDownStationId().equals(downStationId))) {
            throw new SectionInsertException(SAME_SECTION_MESSAGE);
        }
    }

    private void checkNotIncluded(List<Section> sections) {
        if (sections.size() > 0 && !sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(upStationId) || section.getDownStationId().equals(downStationId)
                        || section.getUpStationId().equals(downStationId) || section.getDownStationId().equals(upStationId))) {
            throw new SectionInsertException(STATION_NOT_EXIST_MESSAGE);
        }
    }

    private void checkValidDistance(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new SectionInsertException(DISTANCE_EXCEED_MESSAGE);
        }
    }

    public int getInsertNewDistance(Section section) {
        checkValidDistance(section);
        return distance - section.getDistance();
    }

    public int getDeleteNewDistance(Section section) {
        return distance + section.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
