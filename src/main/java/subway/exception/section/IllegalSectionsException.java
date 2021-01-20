package subway.exception.section;

public class IllegalSectionsException extends SectionException {
    private static final String ILLEGAL_SECTIONS_ERROR = "잘못된 Sections 입니다.";

    public IllegalSectionsException() {
        super(ILLEGAL_SECTIONS_ERROR);
    }
}
