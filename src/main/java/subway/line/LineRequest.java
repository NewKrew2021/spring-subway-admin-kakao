package subway.line;

import subway.exception.exceptions.*;
import subway.section.Section;

public class LineRequest {

    private static final String NONEMPTY_ARGUMENT_MESSAGE = "모든 정보를 입력해주세요.";
    private static final String SAME_STATION_MESSAGE = "상행종점과 하행종점은 같을 수 없습니다.";

    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, long upStationId, long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public Section toSection() {
        return new Section(upStationId, downStationId, distance);
    }

    public void validateLineRequest() {
        validateNonemptyArgument();
        validateDifferentUpDown();
    }

    private void validateNonemptyArgument() {
        if (upStationId == 0 || downStationId == 0 || distance == 0) {
            throw new FailedSaveException(FailedSaveExceptionEnum.EMPTY_LINE_ARGUMENT);
        }
    }

    private void validateDifferentUpDown() {
        if (upStationId == downStationId) {
            throw new FailedSaveException(FailedSaveExceptionEnum.SAME_STATION);
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
