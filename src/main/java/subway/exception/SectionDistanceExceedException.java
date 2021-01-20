package subway.exception;

public class SectionDistanceExceedException extends RuntimeException{

    public static final String SECTION_DISTANCE_EXCEEDED_MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음";

    private String message;

    public SectionDistanceExceedException(){
        super(SECTION_DISTANCE_EXCEEDED_MESSAGE);
    }

    public SectionDistanceExceedException(String message) {
        super(message);
    }
}
