package subway.exception;

public class EmptySectionException extends Exception{
    public EmptySectionException() {
        super("섹션을 지울수 없습니다!");
    }
}
