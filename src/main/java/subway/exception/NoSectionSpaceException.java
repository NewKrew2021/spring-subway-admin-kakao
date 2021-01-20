package subway.exception;

public class NoSectionSpaceException extends Exception{
    public NoSectionSpaceException() {
        super("섹션을 추가할 위치를 찾지 못했습니다!");
    }
}
