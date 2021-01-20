package subway.exception.custom;

public class IllegalDistanceException extends RuntimeException {
    public IllegalDistanceException() {
        super("구간의 거리가 잘못 설정되었습니다.");
    }
}
