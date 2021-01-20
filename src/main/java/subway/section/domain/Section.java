package subway.section.domain;

import subway.line.domain.Line;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long lineId, Long upStationId,
                   Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Line line) {
        this.lineId = line.getId();
        this.upStationId = line.getUpStationId();
        this.downStationId = line.getDownStationId();
        this.distance = line.getDistance();
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

    public Long getLineId() {
        return lineId;
    }

    public static boolean isAddStation(Long sectionRequestStationId, Long lineStationId) {
        return sectionRequestStationId.equals(lineStationId);
    }
}
