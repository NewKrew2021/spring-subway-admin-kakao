package subway.exceptions.stationExceptions;

public class StationDeleteException extends RuntimeException {
    public StationDeleteException() {
        super("해당 ID를 가진 역이 존재하지 않아, 삭제할 수 없습니다.");
    }

    public StationDeleteException(String message) {
        super(message);
    }

}
