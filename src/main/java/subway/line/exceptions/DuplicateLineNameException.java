package subway.line.exceptions;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateLineNameException extends DuplicateKeyException {
    private static final String MESSAGE_FORMAT = "%s 노선은 이미 존재합니다.";

    public DuplicateLineNameException(String name) {
        super(String.format(MESSAGE_FORMAT, name));
    }
}
