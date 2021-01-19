package subway.line;

import java.util.*;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance){
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

    public void checkValidDelete(Map<Long, Long> sections) {
        checkDeleteLastSection(sections);
    }

    private void checkSameSection(List<Section> sections) {
        if (sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(upStationId) && section.getDownStationId().equals(downStationId))) {
            System.out.println("같은 섹션 에러");
            throw new IllegalArgumentException("같은 section이 추가될 수 없습니다.");
        }
    }
    //TO DO: 테스트 에러발생 지점!!
    private void checkNotIncluded(List<Section> sections) {
        if (sections.size() > 0 && !sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(upStationId) || section.getDownStationId().equals(downStationId)
                || section.getUpStationId().equals(downStationId) || section.getDownStationId().equals(upStationId))) {
            System.out.println("어떤 섹션 에러");
            throw new IllegalArgumentException("어떤 station도 없으면 추가할 수 없습니다.");
        }
    }

    public void checkValidDistance(Section section) {
        if (this.distance >= section.getDistance()) {
            System.out.println("거리 에러");
            throw new IllegalArgumentException("추가하려는 거리가 기존의 거리보다 클 수 없습니다.");
        }
    }

    private void checkDeleteLastSection(Map<Long, Long> sections) {
        if (sections.size() == 1 && (sections.containsKey(upStationId) || sections.containsValue(downStationId))) {
            throw new IllegalArgumentException("마지막 구간은 지울 수 없습니다.");
        }
    }

}
