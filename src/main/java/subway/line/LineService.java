package subway.line;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.station.Station;
import subway.station.StationDao;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LineService {

    @Resource
    private LineDao lineDao;

    @Resource
    private StationDao stationDao;

    @Resource
    private SectionDao sectionDao;

    public ResponseEntity<LineResponse> createLine(LineRequest lineRequest) {
        try {
            lineDao.save(new Line(lineRequest));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.findByName(lineRequest.getName());
        sectionDao.save(new Section(newLine));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine, getStations(newLine.getId())));
    }

    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();

        return ResponseEntity.ok().body(LineResponse.getLineResponses(lines));
    }

    public ResponseEntity deleteLine(Long id) {
        lineDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<LineResponse> showLine(Long id) {
        Line line = lineDao.findById(id);

        return ResponseEntity.ok(new LineResponse(line, getStations(id)));
    }

    public ResponseEntity updateLine(Long id, LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest));

        return ResponseEntity.ok().build();
    }

    public List<Station> getStations(Long id) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.findByLineId(id);
        List<Station> stations = new ArrayList<>();

        Map<Long, Section> orderedSections = Section.getOrderedSections(sections);
        Long upStationId = line.getUpStationId();
        stations.add(stationDao.findById(upStationId));

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()));
            upStationId = section.getDownStationId();
        }

        return stations;
    }

    public void addLastStation(Line line, SectionRequest sectionRequest, Section newSection) {
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                line.getUpStationId(),
                sectionRequest.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);
        sectionDao.save(newSection);
    }

    public void addFirstStation(Line line, SectionRequest sectionRequest, Section newSection) {
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                sectionRequest.getUpStationId(),
                line.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);
        sectionDao.save(newSection);
    }

    public void addDownStation(Map<Long, Section> orderedSections, Line line, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        int distanceSum = 0;

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        line.getId(),
                        upStationId,
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance() - distanceSum);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        line.getId(),
                        sectionRequest.getDownStationId(),
                        section.getDownStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance());

                sectionDao.update(updateSection);
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

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        line.getId(),
                        sectionRequest.getUpStationId(),
                        downStationId,
                        sectionRequest.getDistance() - distanceSum);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        line.getId(),
                        section.getUpStationId(),
                        sectionRequest.getUpStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance());

                sectionDao.update(updateSection);
                return;
            }

            downStationId = section.getUpStationId();
            distanceSum += section.getDistance();
        }
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }


    public ResponseEntity createSection(Long id, SectionRequest sectionRequest) {
        sectionValidator(id, sectionRequest);

        Section newSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        List<Section> sections = sectionDao.findByLineId(id);
        Line line = lineDao.findById(id);

        Map<Long, Section> orderedSections = Section.getOrderedSections(sections);
        Map<Long, Section> reverseOrderedSections = Section.getReverseOrderedSections(sections);

        if (Section.isAddStation(sectionRequest.getUpStationId(), line.getDownStationId())) {
            addLastStation(line, sectionRequest, newSection);
            return ResponseEntity.ok().build();
        }

        if (Section.isAddStation(sectionRequest.getDownStationId(), line.getUpStationId())) {
            addFirstStation(line, sectionRequest, newSection);
            return ResponseEntity.ok().build();
        }

        if (orderedSections.containsKey(sectionRequest.getUpStationId())) {
            addDownStation(orderedSections, line, sectionRequest);
            return ResponseEntity.ok().build();
        }

        if (reverseOrderedSections.containsKey(sectionRequest.getDownStationId())) {
            addUpStation(reverseOrderedSections, line, sectionRequest);
            return ResponseEntity.ok().build();
        }

        throw new IllegalArgumentException();
    }

    private boolean containsEndStation(Long id, SectionRequest sectionRequest) {
        List<Station> stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                || stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    private void sectionValidator(Long id, SectionRequest sectionRequest) {
        if (hasDuplicatedStation(id, sectionRequest)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if (!containsEndStation(id, sectionRequest)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    private boolean hasDuplicatedStation(Long id, SectionRequest sectionRequest) {
        List<Station> stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                && stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    public ResponseEntity deleteSection(Long id, Long stationId) {
        if (sectionDao.countByLineId(id) == 1) {
            throw new DeleteSectionException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음");
        }

        stationDao.deleteById(stationId);

        List<Section> sections = sectionDao.findByStationIdAndLineId(stationId, id);
        Line line = lineDao.findById(id);

        if (line.isEndStation(sections.size())) {
            Section endSection = sections.get(0);
            line.updateEndStation(endSection, stationId);
            sectionDao.deleteById(endSection.getId());
            lineDao.update(line);
            return ResponseEntity.ok().build();
        }

        Section mergeSection = sections.get(0).merge(sections.get(1), stationId);
        sectionDao.deleteById(sections.get(1).getId());
        sectionDao.update(mergeSection);

        return ResponseEntity.ok().build();
    }
}
