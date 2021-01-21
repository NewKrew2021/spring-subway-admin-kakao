package subway.station.dto;

import java.beans.ConstructorProperties;

public class StationRequest {
    private String name;

    // TODO: ConstructorProperties를 parameterNamesModule 로 바꿔보기?
    //       or 기본 생성자 생성 후 private?
    @ConstructorProperties("name")
    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
