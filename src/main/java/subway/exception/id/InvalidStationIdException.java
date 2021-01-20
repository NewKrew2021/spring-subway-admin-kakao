package subway.exception.id;

public class InvalidStationIdException extends InvalidIdException {
    public static final String INVALID_STATION_ID_ERROR = "존재하지 않는 Station ID 입니다. Station ID : ";

    public InvalidStationIdException(Long id) {
        super(INVALID_STATION_ID_ERROR + id);
    }
}
