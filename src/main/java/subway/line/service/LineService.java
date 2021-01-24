package subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.domain.LineRequest;
import subway.line.domain.LineResponse;
import subway.section.dao.SectionDao;
import subway.section.domain.Section;
import subway.section.domain.SectionRequest;
import subway.section.domain.Sections;
import subway.station.dao.StationDao;
import subway.station.domain.Stations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final int SECTION_DISTANCE_SUM_DEFAULT = 0;
    private final String DUPLICATED_STATION_BOTH_SECTION = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";
    private final String NOT_CONTAIN_STATION_BOTH_SECTION = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";
    private final String NOT_DELETE_SECTION = "구간이 하나인 노선에서 마지막 구간을 제거할 수 없음";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest);

        Long lineId = lineDao.save(line);

        Line newLine = lineDao.findById(lineId);

        Long sectionId = sectionDao.save(new Section(newLine));

        return new LineResponse(newLine, getStations(sectionId));
    }

    public List<LineResponse> showLines() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    public LineResponse showLine(Long id) {
        return new LineResponse(lineDao.findById(id), getStations(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        lineDao.update(Line.getLineToLineRequest(id, lineRequest));
    }

    public Stations getStations(Long id) {
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionDao.findByLineId(id));
        Stations stations = new Stations();

        Map<Long, Section> orderedSections = sections.getOrderedSections();
        Long upStationId = line.getUpStationId();

        stations.add(stationDao.findById(upStationId));

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()));
            upStationId = section.getDownStationId();
        }

        return stations;
    }

    @Transactional
    public void addLastStation(Line line, SectionRequest sectionRequest, Section newSection) {
        // LineDao에서 해당 라인의 downStationId와 distance를 업데이트
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                line.getUpStationId(),
                sectionRequest.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);

        // SectionDao에서 구간 추가
        sectionDao.save(newSection);
    }

    @Transactional
    public void addFirstStation(Line line, SectionRequest sectionRequest, Section newSection) {
        // LineDao에서 해당 라인의 downStationId와 distance를 업데이트
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                sectionRequest.getUpStationId(),
                line.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);

        // SectionDao에서 구간 추가
        sectionDao.save(newSection);
    }

    @Transactional
    public void addDownStation(Map<Long, Section> orderedSections, Line line, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        int distanceSum = SECTION_DISTANCE_SUM_DEFAULT;

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException();
            }

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        line.getId(),
                        upStationId,
                        sectionRequest.getDownStationId(), sectionRequest.getDistance() - distanceSum);

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
        throw new SectionDistanceExceedException();
    }

    @Transactional
    public void addUpStation(Map<Long, Section> reverseOrderedSections, Line line, SectionRequest sectionRequest) {
        Long downStationId = sectionRequest.getDownStationId();
        int distanceSum = SECTION_DISTANCE_SUM_DEFAULT;

        while (reverseOrderedSections.containsKey(downStationId)) {
            Section section = reverseOrderedSections.get(downStationId);

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException();
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

        throw new SectionDistanceExceedException();
    }

    @Transactional
    public void createSection(Long id, SectionRequest sectionRequest) {
        sectionValidator(id, sectionRequest);

        Section newSection = new Section(id, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(id));
        Line line = lineDao.findById(id);

        Map<Long, Section> orderedSections = sections.getOrderedSections();
        Map<Long, Section> reverseOrderedSections = sections.getReverseOrderedSections();

        // 하행 종점 변경 (A -> B -> (C))
        if (Section.isAddStation(sectionRequest.getUpStationId(), line.getDownStationId())) {
            addLastStation(line, sectionRequest, newSection);
            return;
        }

        // 상행 종점 변경 ((C) -> A -> B)
        if (Section.isAddStation(sectionRequest.getDownStationId(), line.getUpStationId())) {
            addFirstStation(line, sectionRequest, newSection);
            return;
        }

        // 하행역 추가
        if (orderedSections.containsKey(sectionRequest.getUpStationId())) {
            addDownStation(orderedSections, line, sectionRequest);
            return;
        }

        // 상행역 추가
        if (reverseOrderedSections.containsKey(sectionRequest.getDownStationId())) {
            addUpStation(reverseOrderedSections, line, sectionRequest);
            return;
        }

        throw new IllegalArgumentException("생성 실패");
    }

    private boolean containsEndStation(Long id, SectionRequest sectionRequest) {
        Stations stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                || stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    private void sectionValidator(Long id, SectionRequest sectionRequest) {
        if (hasDuplicatedStation(id, sectionRequest)) {
            throw new IllegalArgumentException(DUPLICATED_STATION_BOTH_SECTION);
        }

        if (!containsEndStation(id, sectionRequest)) {
            throw new IllegalArgumentException(NOT_CONTAIN_STATION_BOTH_SECTION);
        }
    }

    private boolean hasDuplicatedStation(Long id, SectionRequest sectionRequest) {
        Stations stations = getStations(id);

        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                && stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
        //@TODO 삭제
        // 1. stationId로 구간 조회
        // 2. 해당 역이 상행, 하행 종점일 경우 구간 삭제 + 노선의 상행, 하행 업데이트 (sectionDao, LineDao)
        // 3. 중간에 갈래길일 경우, 두개의 구간을 하나의 구간으로 통합
        // 4. 역애 대한 정보 삭제 (stationDao)

        Sections sections = new Sections(sectionDao.findByLineId(id));
        if (sections.size() == Line.END_STATION_SECTION_SIZE) {
            throw new DeleteSectionException(NOT_DELETE_SECTION);
        }

        // 1.
        Sections updateSections = new Sections(sectionDao.findByStationIdAndLineId(stationId, id));

        Line line = lineDao.findById(id);

        // 2.
        if (line.isEndStation(updateSections.size())) {
            Section endSection = updateSections.get(0);
            line.updateEndStation(endSection, stationId);
            sectionDao.deleteById(endSection.getId());
        }

        // 3.
        if (!line.isEndStation(updateSections.size())) {
            sectionDao.deleteBySections(updateSections);
            sectionDao.save(updateSections.merge(stationId));
        }

        // 4.
        stationDao.deleteById(stationId);
        lineDao.update(line);
    }
}
