package subway.exception;

public class BothExistSectionException extends RuntimeException{
    private static final String BOTH_EXIST_SECTION_EXCEPTION = "추가하려는 구간의 역이 노선에 둘 다 존재합니다.";

    public BothExistSectionException() {
        super(BOTH_EXIST_SECTION_EXCEPTION);
    }
}
