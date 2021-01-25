package subway.exception.section;

public class SectionAttachException extends SectionException {
    private static final String SECTION_ATTACH_ERROR = "Section의 하행과 붙이는 Section의 상행이 같아야 합니다.";

    public SectionAttachException() {
        super(SECTION_ATTACH_ERROR);
    }
}
