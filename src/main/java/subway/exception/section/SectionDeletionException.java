package subway.exception.section;

public class SectionDeletionException extends SectionException {
    private static final String SECTION_DELETE_ERROR_ONE_SECTION = "Section이 하나만 존재하여 삭제할 수 없습니다.";

    public SectionDeletionException() {
        super(SECTION_DELETE_ERROR_ONE_SECTION);
    }
}
