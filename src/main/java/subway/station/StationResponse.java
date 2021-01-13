package subway.station;

public class StationResponse {
    private Long id;
    private String name;

    // TODO 뭔진 모르겠지만 default 생성자가 필요.
    public StationResponse() {
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
