package subway.exceptions.stationExceptions;

public class StationDuplicateException extends RuntimeException {
    public StationDuplicateException() {
        super("입력된 이름의 역이 이미 존재합니다.");
    }

    public StationDuplicateException(String message) {
        super(message);
    }

}
