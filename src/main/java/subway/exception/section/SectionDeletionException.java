package subway.exception.section;

public class SectionDeletionException extends SectionException {
    private static final String SECTION_DELETE_ERROR_ONE_SECTION = "Section 삭제 중 오류가 발생했습니다.";

    public SectionDeletionException() {
        super(SECTION_DELETE_ERROR_ONE_SECTION);
    }
}
