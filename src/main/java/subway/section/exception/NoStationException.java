package subway.section.exception;

public class NoStationException extends IllegalArgumentException{
    public NoStationException() {
        super("구간의 역들중 하나의 역이라도 일치해야합니다.");
    }
}
