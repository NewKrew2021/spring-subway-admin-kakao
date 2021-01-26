package subway.section.exception;

import subway.exception.AlreadyExistsException;

public class SectionBetweenStationExistException extends AlreadyExistsException {
    public SectionBetweenStationExistException() {
        super("추가하려는 구간 사이에 이미 역이 존재합니다.");
    }
}
