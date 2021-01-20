package subway.exception.section;

public class InvalidStationException extends SectionException {
    private static final String SECTION_DELETE_ERROR_NO_STATION = "Sections에 포함되지 않는 Station 입니다. Station ID : ";

    public InvalidStationException(Long stationId) {
        super(SECTION_DELETE_ERROR_NO_STATION + stationId);
    }
}
