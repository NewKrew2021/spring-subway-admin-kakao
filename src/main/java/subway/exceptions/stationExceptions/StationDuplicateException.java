package subway.exceptions.stationExceptions;

public class StationDuplicateException extends RuntimeException {
    @Override
    public String getMessage() {
        return "입력된 이름의 역이 이미 존재합니다.";
    }
}
