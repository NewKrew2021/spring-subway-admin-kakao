package subway.line.domain;

public class SectionNotValidDeleteException extends RuntimeException {
    public SectionNotValidDeleteException(String message) {
        super(message);
    }
}
