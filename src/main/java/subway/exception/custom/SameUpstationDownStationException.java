package subway.exception.custom;

public class SameUpstationDownStationException extends RuntimeException {
    public SameUpstationDownStationException() {
        super("추가하고자 하는 구간의 상행역과 하행역이 같습니다.");
    }
}
