package subway.exception;


public class DuplicateNameException extends IllegalArgumentException{
    public DuplicateNameException() {
        super("이미 존재하는 역 이름 입니다.");
    }
}
