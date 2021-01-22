package subway.exception;

public class ExistLineSaveException extends RuntimeException{
    private static final String EXIST_LINE_SAVE_EXCEPTION = "추가하려는 노선이 이미 존재합니다.";

    public ExistLineSaveException() {
        super(EXIST_LINE_SAVE_EXCEPTION);
    }
}
