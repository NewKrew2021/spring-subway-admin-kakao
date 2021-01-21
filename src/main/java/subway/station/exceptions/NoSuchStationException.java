package subway.station.exceptions;

import java.util.NoSuchElementException;

public class NoSuchStationException extends NoSuchElementException {
    private static final String MESSAGE_FORMAT = "id:%d station을 찾을 수 없습니다.";

    public NoSuchStationException(Long stationId) {
        super(String.format(MESSAGE_FORMAT, stationId));
    }
}
