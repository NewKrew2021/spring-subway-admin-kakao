package subway.exception;

public class StationNotFoundException extends EntityNotFoundException {
    public StationNotFoundException(Long id) {
        super(String.format("id=%d인 역이 존재하지 않습니다", id));
    }
}
