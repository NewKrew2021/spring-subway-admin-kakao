package subway.exception;

public class SectionCreateException extends RuntimeException {
    public static final String DISTANCE_EXCEPTION = "Section의 거리는 양수여야 합니다. 거리 : ";
    public static final String DUPLICATE_STATION_EXCEPTION = "상행 Station과 하행 Station이 동일합니다.";

    public SectionCreateException(String message) {
        super(message);
    }
}
