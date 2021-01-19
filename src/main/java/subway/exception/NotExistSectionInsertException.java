package subway.exception;

public class NotExistSectionInsertException extends RuntimeException{
    private static final String NOT_EXIST_SECTION_EXCEPTION = "존재하지 않는 구간을 추가요청 하였습니다.";

    public NotExistSectionInsertException() {
        super(NOT_EXIST_SECTION_EXCEPTION);
    }
}
