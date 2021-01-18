package subway.line;

import org.springframework.http.ResponseEntity;
import subway.station.Station;
import subway.station.StationDao;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class LineService {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public LineService() {
        this.lineDao = LineDao.getInstance();
        this.stationDao = StationDao.getInstance();
        this.sectionDao = SectionDao.getInstance();
    }

    public ResponseEntity<LineResponse> createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest);
        Line newLine;

        try {
            newLine = lineDao.save(line);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }

        Section section = new Section(newLine);
        sectionDao.save(section);
//        LineResponse lineResponse = new LineResponse(newLine.getId(),newLine.getName(),newLine.getColor(),newLine.getStations());
        LineResponse lineResponse = new LineResponse(newLine, getStations(newLine.getId()));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream().map(LineResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    public ResponseEntity deleteLine(Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<LineResponse> showLine(Long id) {
        Optional<Line> lineOptional = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(lineOptional.get(), getStations(id));
        return ResponseEntity.ok(lineResponse);
    }

    public ResponseEntity updateLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    public List<Station> getStations(Long id) {
        Line line = lineDao.findById(id).get();
        List<Section> sections = sectionDao.findByLineId(id);
        List<Station> stations = new ArrayList<>();

        Map<Long, Section> orderedSections = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        Long upStationId = line.getUpStationId();

        stations.add(stationDao.findById(upStationId).get());

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()).get());
            upStationId = section.getDownStationId();
        }

        return stations;
    }

    public boolean isAddStation(Long sectionRequestStationId, Long lineStationId) {
        return sectionRequestStationId.equals(lineStationId);
    }

    public void addLastStation(Line line, SectionRequest sectionRequest, Section newSection) {
        // LineDao에서 해당 라인의 downStationId와 distance를 업데이트
        Line updateLine = new Line(line.getId(),
                line.getUpStationId(),
                sectionRequest.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);

        // SectionDao에서 구간 추가
        sectionDao.save(newSection);
    }

    public void addFirstStation(Line line, SectionRequest sectionRequest, Section newSection) {
        // LineDao에서 해당 라인의 downStationId와 distance를 업데이트
        Line updateLine = new Line(line.getId(),
                sectionRequest.getUpStationId(),
                line.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);

        // SectionDao에서 구간 추가
        sectionDao.save(newSection);
    }

    public void addDownStation(Map<Long, Section> orderedSections, Line line, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        int distanceSum = 0;

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        line.getId(),
                        upStationId,
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance() - distanceSum);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        line.getId(),
                        sectionRequest.getDownStationId(),
                        section.getDownStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance());

                sectionDao.update(section.getId(), updateSection);
                return;
            }

            upStationId = section.getDownStationId();
            distanceSum += section.getDistance();
        }
    }

    public void addUpStation(Map<Long, Section> reverseOrderedSections, Line line, SectionRequest sectionRequest) {
        Long downStationId = sectionRequest.getDownStationId();
        int distanceSum = 0;

        while (reverseOrderedSections.containsKey(downStationId)) {
            Section section = reverseOrderedSections.get(downStationId);

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        line.getId(),
                        sectionRequest.getUpStationId(),
                        downStationId,
                        sectionRequest.getDistance() - distanceSum);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        line.getId(),
                        section.getUpStationId(),
                        sectionRequest.getUpStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance());

                sectionDao.update(section.getId(), updateSection);
                return;
            }

            downStationId = section.getUpStationId();
            distanceSum += section.getDistance();
        }
    }


    public ResponseEntity createSection(Long id, SectionRequest sectionRequest) {
        Section newSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        List<Section> sections = sectionDao.findByLineId(id);
        Line line = lineDao.findById(id).get();

        Map<Long, Section> orderedSections = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        Map<Long, Section> reverseOrderedSections = sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));

        // 하행 종점 변경 (A -> B -> (C))
        if (isAddStation(sectionRequest.getUpStationId(), line.getDownStationId())) {
            addLastStation(line, sectionRequest, newSection);
            return ResponseEntity.ok().build();
        }

        // 상행 종점 변경 ((C) -> A -> B)
        if (isAddStation(sectionRequest.getDownStationId(), line.getUpStationId())) {
            addFirstStation(line, sectionRequest, newSection);
            return ResponseEntity.ok().build();
        }

        // 하행역 추가
        if (orderedSections.containsKey(sectionRequest.getUpStationId())) {
            addDownStation(orderedSections, line, sectionRequest);
            return ResponseEntity.ok().build();
        }

        // 상행역 추가
        if (reverseOrderedSections.containsKey(sectionRequest.getDownStationId())) {
            addUpStation(reverseOrderedSections, line, sectionRequest);
            return ResponseEntity.ok().build();
        }


        return ResponseEntity.ok().build();
    }

}
