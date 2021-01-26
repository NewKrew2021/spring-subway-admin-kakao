package subway.section.exception;

import subway.exception.AlreadyExistsException;

public class SectionAlreadyExistException extends AlreadyExistsException {
    public SectionAlreadyExistException() {
        super("추가하려는 구간이 이미 존재합니다.");
    }
}
