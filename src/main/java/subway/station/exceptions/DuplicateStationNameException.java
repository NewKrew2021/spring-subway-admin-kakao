package subway.station.exceptions;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateStationNameException extends DuplicateKeyException {

    public DuplicateStationNameException(String msg) {
        super(msg);
    }
}
