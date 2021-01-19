package subway.exception;

public class DuplicateLineNameException extends Exception{
    private final static String DUPLICATE_LINE_NAME_MESSAGE = "노선 이름이 중복 될 수 없습니다.";

    public DuplicateLineNameException(){
        super(DUPLICATE_LINE_NAME_MESSAGE);
    }
}
