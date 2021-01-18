package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.station.Station;
import subway.station.StationDao;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {

    @Resource
    private LineDao lineDao;

    @Resource
    private StationDao stationDao;

    @Resource
    private SectionDao sectionDao;

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

        stations.add(stationDao.findById(upStationId));

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()));
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
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
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
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }


    public ResponseEntity createSection(Long id, SectionRequest sectionRequest) {
        //

        Section newSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        List<Section> sections = sectionDao.findByLineId(id);
        Line line = lineDao.findById(id).get();

        Map<Long, Section> orderedSections = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        Map<Long, Section> reverseOrderedSections = sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));

        if (hasDuplicatedStation(id, sectionRequest)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if (!containsEndStation(id, sectionRequest)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }

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


        throw new SectionDistanceExceedException("생성 실패");
    }

    private boolean containsEndStation(Long id, SectionRequest sectionRequest) {
        List<Station> stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                || stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    private boolean hasDuplicatedStation(Long id, SectionRequest sectionRequest) {
        List<Station> stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                && stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    public ResponseEntity deleteSection(Long id, Long stationId) {
        //@TODO 삭제
        // 1. stationId로 구간 조회
        // 2. 해당 역이 상행, 하행 종점일 경우 구간 삭제 + 노선의 상행, 하행 업데이트 (sectionDao, LineDao)
        // 3. 중간에 갈래길일 경우, 두개의 구간을 하나의 구간으로 통합
        // 4. 역애 대한 정보 삭제 (stationDao)

        List<Section> sections = sectionDao.findByLineId(id);
        if (sections.size() == 1) {
            throw new DeleteSectionException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음");
        }

        // 1.
        List<Section> sectionList = sectionDao.findByStationIdAndLineId(stationId,id);
        Line line = lineDao.findById(id).get();

        // 2.
        if(line.isEndStation(sectionList.size())){
            Section endSection = sectionList.get(0);
            line.updateEndStation(endSection,stationId);
            sectionDao.deleteById(endSection.getId());
        }

        // 3.
        if(!line.isEndStation(sectionList.size())){
            Section mergeSection = sectionList.get(0).merge(sectionList.get(1), stationId);
            sectionDao.deleteById(sectionList.get(1).getId());
        }

        // 4.
        stationDao.deleteById(stationId);


        return ResponseEntity.ok().build();
    }
}
