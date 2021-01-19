package subway.exception;

public class DuplicateStationNameException extends Exception{
    private static final String DUPLICATE_STATION_NAME_MESSAGE = "지하철 역의 이름이 중복 될 수 없습니다.";

    public DuplicateStationNameException(){
        super(DUPLICATE_STATION_NAME_MESSAGE);
    }
}
