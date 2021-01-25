package subway.exception.section;

public class StationDuplicationException extends SectionException {
    private static final String DUPLICATE_STATION_EXCEPTION = "상행 Station과 하행 Station이 동일합니다.";

    public StationDuplicationException() {
        super(DUPLICATE_STATION_EXCEPTION);
    }
}
