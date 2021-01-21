package subway.station.exceptions;

public class InvalidStationDeleteException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "id:%d station을 삭제할 수 없습니다.";

    public InvalidStationDeleteException(Long id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
