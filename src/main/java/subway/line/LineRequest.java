package subway.line;

import subway.exceptions.InvalidLineArgumentException;
import subway.section.Section;

public class LineRequest {

    private static final String EMPTY_ARGUMENT_ERROR_MESSAGE = "모든 정보를 입력해주세요.";
    private static final String SAME_STATION_ERROR_MESSAGE = "상행종점과 하행종점은 같을 수 없습니다.";

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void checkLineRequest() {
        checkEmptyArgument();
        checkStationId();
    }

    private void checkEmptyArgument() {
        if (upStationId == null || downStationId == null || distance == 0) {
            throw new InvalidLineArgumentException(EMPTY_ARGUMENT_ERROR_MESSAGE);
        }
    }

    private void checkStationId() {
        if (upStationId == downStationId) {
            throw new InvalidLineArgumentException(SAME_STATION_ERROR_MESSAGE);
        }
    }

    public Line toLine() {
        return new Line(name, color, upStationId, downStationId);
    }

    public Line toLine(Long id, Long upStationId, Long downStationId) { return new Line(id, name, color, upStationId, downStationId);}

    public Section toSection(Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
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
}
