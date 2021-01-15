package subway.line;

import subway.section.Section;

import java.util.List;

public interface LineService {
    public Line save(Line line, Section section);

    public boolean deleteById(Long lineId);

    public List<Line> findAll();

    public Line findOne(Long lineId);

    public void update(Line line);

    public boolean saveSection(Long lineId, Section section);

    public boolean deleteSection(Long lineId, Long stationId);
}
