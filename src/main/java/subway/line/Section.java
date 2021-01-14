package subway.line;

public class Section {
    private Long id;
    private final Long line_id;
    private final Long up_station_id;
    private final Long down_station_id;
    private final int distance;

    public Section(Long line_id, Long up_station_id, Long down_station_id, int distance) {
        this.line_id = line_id;
        this.up_station_id = up_station_id;
        this.down_station_id = down_station_id;
        this.distance = distance;
    }

    public Section(Long id, Long line_id, Long up_station_id, Long down_station_id, int distance) {
        this.id = id;
        this.line_id = line_id;
        this.up_station_id = up_station_id;
        this.down_station_id = down_station_id;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLine_id() {
        return line_id;
    }

    public Long getUp_station_id() {
        return up_station_id;
    }

    public Long getDown_station_id() {
        return down_station_id;
    }

    public int getDistance() {
        return distance;
    }
}
