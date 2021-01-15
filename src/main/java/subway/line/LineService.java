package subway.line;

public interface LineService {
    public LineResponse save(Line line, Section section);
    public boolean deleteById(Long lineId);
}
