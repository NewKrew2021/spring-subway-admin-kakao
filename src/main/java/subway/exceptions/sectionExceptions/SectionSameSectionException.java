package subway.exceptions.sectionExceptions;

public class SectionSameSectionException extends RuntimeException {
    public SectionSameSectionException() {
        super("상행역과 하행역이 모두 노선에 포함되어 있습니다.");
    }

    public SectionSameSectionException(String message) {
        super(message);
    }

}
