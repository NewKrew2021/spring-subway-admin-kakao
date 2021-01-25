package subway.exception;

public class NotEnoughLengthToDeleteSectionException extends IllegalStateException{
    private static final String NOT_ENOUGH_LENGTH_TO_DELETE_SECTION_MESSAGE = "섹션을 삭제하기 위해서는 섹션이 2개 이상이여야 합니다.";

    public NotEnoughLengthToDeleteSectionException(){
        super(NOT_ENOUGH_LENGTH_TO_DELETE_SECTION_MESSAGE);
    }
}
