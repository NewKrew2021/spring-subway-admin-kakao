package subway.exceptions.sectionExceptions;

public class SectionNoStationException extends RuntimeException {
    public SectionNoStationException() {
        super("상행역과 하행역이 모두 노선에 포함되어 있습니다.");
    }

    public SectionNoStationException(String message) {
        super(message);
    }

}
