package subway.station.dto;

import java.beans.ConstructorProperties;

public class StationResponse {
    private Long id;
    private String name;

    // TODO: ConstructorProperties를 parameterNamesModule 로 바꿔보기?
    //       or 기본 생성자 생성 후 private?
    @ConstructorProperties({"id", "name"})
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
