package subway.section;

import java.util.List;

public class Section {
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
            throw new IllegalArgumentException("같은 section이 추가될 수 없습니다.");
        }
    }

    private void checkNotIncluded(List<Section> sections) {
        if (sections.size() > 0 && !sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(upStationId) || section.getDownStationId().equals(downStationId)
                        || section.getUpStationId().equals(downStationId) || section.getDownStationId().equals(upStationId))) {
            throw new IllegalArgumentException("어떤 station도 없으면 추가할 수 없습니다.");
        }
    }

    private void checkValidDistance(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("추가하려는 거리가 기존의 거리보다 클 수 없습니다.");
        }
    }

    public int getInsertNewDistance(Section section) {
        checkValidDistance(section);
        return distance - section.getDistance();
    }

    public int getDeleteNewDistance(Section section) {
        return distance + section.getDistance();
    }

}
