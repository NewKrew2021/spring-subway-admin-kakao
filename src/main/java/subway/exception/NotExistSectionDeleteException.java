package subway.exception;

public class NotExistSectionDeleteException extends RuntimeException{

    private static final String NOT_EXIST_SECTION_EXCEPTION = "존재하지 않는 구간을 삭제요청 하였습니다.";

    public NotExistSectionDeleteException() {
        super(NOT_EXIST_SECTION_EXCEPTION);
    }

}
