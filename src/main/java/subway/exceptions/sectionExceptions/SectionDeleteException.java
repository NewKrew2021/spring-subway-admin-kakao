package subway.exceptions.sectionExceptions;

public class SectionDeleteException extends RuntimeException {
    public SectionDeleteException() {
        super("노선에 역이 존재하지 않거나, 노선에 역이 현재 2개뿐이 없습니다.");
    }

    public SectionDeleteException(String message) {
        super(message);
    }

}
