package subway.exceptions.sectionExceptions;

public class SectionIllegalDistanceException extends RuntimeException {
    public SectionIllegalDistanceException() {
        super("입력된 거리가 올바르지 않습니다.");
    }

    public SectionIllegalDistanceException(String message) {
        super(message);
    }

}
