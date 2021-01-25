package subway.exception;

public class LineNotFoundException extends IllegalArgumentException{
    private final static String LINE_NOT_FOUND_MESSAGE = "해당 노선을 찾을 수 없습니다";

    public LineNotFoundException(){
        super(LINE_NOT_FOUND_MESSAGE);
    }
}
