package subway.exception.custom;

public class IllegalSectionException extends RuntimeException {
    public IllegalSectionException() {
        super("지하철 구간이 잘못 설정되었습니다.");
    }
}