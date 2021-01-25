package subway.exception.section;

public class SectionSplitException extends SectionException {
    private static final String SECTION_SPLIT_ERROR = "Section은 하나의 역만 같아야 합니다.";

    public SectionSplitException() {
        super(SECTION_SPLIT_ERROR);
    }
}
