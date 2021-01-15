package subway.response;

import subway.domain.Section;

public class SectionResponse {
    private Long id;
    private Long line_id;
    private Long up_station_id;
    private Long down_station_id;
    private int distance;

    public SectionResponse() {

    }

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.line_id = section.getLineId();
        this.up_station_id = section.getUpStationId();
        this.down_station_id = section.getDownStationId();
        this.distance = section.getDistance();
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
