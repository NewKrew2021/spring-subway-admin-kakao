package subway.exception.section;

public class SectionDistanceException extends SectionException {
    private static final String DISTANCE_EXCEPTION = "Section의 거리는 양수여야 합니다. 거리 : ";

    public SectionDistanceException(int distance) {
        super(DISTANCE_EXCEPTION + distance);
    }
}
