package subway.line;

import java.util.Objects;

public class Section {
    private final Long line_id;
    private final Long up_station_id;
    private final Long down_station_id;
    private final int distance;
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(line_id, section.line_id) && Objects.equals(up_station_id, section.up_station_id) && Objects.equals(down_station_id, section.down_station_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line_id, up_station_id, down_station_id, distance);
    }
}
