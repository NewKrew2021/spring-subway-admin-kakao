package subway.station.exceptions;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateStationNameException extends DuplicateKeyException {

    private static final String MESSAGE_FORMAT = "%s역은 이미 존재합니다.";

    public DuplicateStationNameException(String stationName) {
        super(String.format(MESSAGE_FORMAT, stationName));
    }
}
