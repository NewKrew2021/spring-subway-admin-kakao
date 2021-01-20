package subway.section;

public class NamedSection extends Section {

    private final String downStationName;

    public NamedSection(Long id, Long lineId, Long upStationId, Long downStationId, int distance, String downStationName) {
        super(id, lineId, upStationId, downStationId, distance);
        this.downStationName = downStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

}
