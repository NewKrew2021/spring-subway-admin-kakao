package subway.section.exceptions;

public class EmptySectionsException extends RuntimeException {
    public EmptySectionsException() {
        super("sections에는 최소 1개 이상의 section이 존재해야합니다.");
    }
}
