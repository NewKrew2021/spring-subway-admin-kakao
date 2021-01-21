package subway.exceptions.stationExceptions;

public class StationDeleteException extends RuntimeException {
    @Override
    public String getMessage() {
        return "해당 ID를 가진 역이 존재하지 않아, 삭제할 수 없습니다.";
    }
}
