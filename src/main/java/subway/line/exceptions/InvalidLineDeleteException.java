package subway.line.exceptions;

public class InvalidLineDeleteException extends RuntimeException{
    private static final String MESSAGE_FORMAT = "id:%d line을 삭제할 수 없습니다.";

    public InvalidLineDeleteException(Long id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
