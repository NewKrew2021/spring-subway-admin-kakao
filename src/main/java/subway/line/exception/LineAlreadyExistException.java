package subway.line.exception;

import subway.exception.AlreadyExistsException;

public class LineAlreadyExistException extends AlreadyExistsException {
    public LineAlreadyExistException() {
        super("같은 이름으로 존재하는 노선이 존재합니다.");
    }
}
