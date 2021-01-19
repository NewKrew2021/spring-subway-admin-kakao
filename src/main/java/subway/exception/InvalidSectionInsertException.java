package subway.exception;

public class InvalidSectionInsertException extends Exception{
    private static final String INVALID_SECTION_INSERT_MESSAGE = "삽입할 수 없는 섹션을 삽일하여 합니다.";

    public InvalidSectionInsertException(){
        super(INVALID_SECTION_INSERT_MESSAGE);
    }
}
