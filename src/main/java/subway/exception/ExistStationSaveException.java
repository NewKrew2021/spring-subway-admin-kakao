package subway.exception;

public class ExistStationSaveException extends RuntimeException {
    private static final String EXIST_STATION_SAVE_EXCEPTION = "추가하려는 역이 이미 존재합니다.";

    public ExistStationSaveException() {
        super(EXIST_STATION_SAVE_EXCEPTION);
    }
}
