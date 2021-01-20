package subway.exception;

public class SectionOperationException extends RuntimeException {
    public static final String SECTION_SPLIT_ERROR = "Section은 하나의 역만 같아야 합니다.";
    public static final String SECTION_ATTACH_ERROR = "Section의 하행과 붙이는 Section의 상행이 같아야 합니다.";
    public static final String SECTION_DELETE_ERROR_ONE_SECTION = "Section이 하나만 존재하여 삭제할 수 없습니다.";
    public static final String SECTION_DELETE_ERROR_NO_STATION = "Sections에 포함되지 않는 Station 입니다.";

    public SectionOperationException(String message) {
        super(message);
    }
}
