package subway.exception;

public class StationNotFoundException extends IllegalArgumentException{
    private final static String STATION_NOT_FOUND_MESSAGE = "존재하지 않는 지하철역(station) 입니다";

    public StationNotFoundException(){
        super(STATION_NOT_FOUND_MESSAGE);
    }
}
