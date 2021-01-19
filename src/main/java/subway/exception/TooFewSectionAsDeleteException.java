package subway.exception;

public class TooFewSectionAsDeleteException extends RuntimeException{
    private static final String TOO_FEW_SECTIONS_AS_DELETE_EXCEPTION = "삭제하기에 구간이 너무 적습니다. (2개 이하)";

    public TooFewSectionAsDeleteException() {
        super(TOO_FEW_SECTIONS_AS_DELETE_EXCEPTION);
    }
}
