package subway.exception.custom;

public class DifferentLineIdException extends RuntimeException {
    public DifferentLineIdException() {
        super("작업을 요청한 노선 아이디와 노선 자원의 주소가 다릅니다.");
    }
}
