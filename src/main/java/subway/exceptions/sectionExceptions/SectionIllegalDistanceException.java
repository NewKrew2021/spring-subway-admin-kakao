package subway.exceptions.sectionExceptions;

public class SectionIllegalDistanceException extends RuntimeException {
    @Override
    public String getMessage() {
        return "입력된 거리가 올바르지 않습니다.";
    }
}
