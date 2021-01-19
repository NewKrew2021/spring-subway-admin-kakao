package subway.line;

public class Line {
    private static final int END_STATION_SECTION_SIZE = 1;
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color, upStationId, downStationId, distance);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public boolean isEndStation(int sectionListSize) {
        return sectionListSize == END_STATION_SECTION_SIZE;
    }

    public void updateEndStation(Section endSection, Long stationId) {
        if (stationId == this.upStationId) {
            this.upStationId = endSection.getDownStationId();
        }

        if (stationId == this.downStationId) {
            this.downStationId = endSection.getUpStationId();
        }
    }

    public static Line getLineToLineRequest(Long id, LineRequest lineRequest) {
        return new Line(id, lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }
}
