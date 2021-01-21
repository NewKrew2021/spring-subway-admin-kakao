package subway.exception.station;

public class DuplicateStationNameException extends RuntimeException {
    private static final String DUPLICATE_NAME_ERROR = "Station이 중복된 이름을 가지고 있습니다.";

    public DuplicateStationNameException() {
        super(DUPLICATE_NAME_ERROR);
    }
}
