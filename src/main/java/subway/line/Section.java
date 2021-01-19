package subway.line;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Section(Line line) {
        this(line.getId(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
    }

    public Long getId() {
        return id;
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

    public Section merge(Section section, Long stationId) {
        if (this.upStationId == stationId) {
            this.upStationId = section.getUpStationId();
        }

        if (this.downStationId == stationId) {
            this.downStationId = section.getDownStationId();
        }

        this.distance += section.getDistance();

        return this;
    }

    public static Map<Long, Section> getOrderedSections(List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
    }

    public static Map<Long, Section> getReverseOrderedSections(List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));
    }

    public static boolean isAddStation(Long sectionRequestStationId, Long lineStationId) {
        return sectionRequestStationId.equals(lineStationId);
    }
}
