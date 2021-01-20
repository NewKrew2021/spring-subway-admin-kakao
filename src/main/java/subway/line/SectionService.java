package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.station.Station;
import subway.station.StationDao;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {

    @Resource
    private LineDao lineDao;
    @Resource
    private StationDao stationDao;
    @Resource
    private SectionDao sectionDao;

    public void create(Section section) {
        sectionDao.save(section);
    }

    public void create(Long lineId, SectionRequest sectionRequest) {
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        List<Section> sections = sectionDao.findByLineId(lineId);
        Line line = lineDao.findById(lineId);

        Map<Long, Section> orderedSections = Section.getOrderedSections(sections);
        Map<Long, Section> reverseOrderedSections = Section.getReverseOrderedSections(sections);

        if (Section.isAddStation(sectionRequest.getUpStationId(), line.getDownStationId())) {
            addLastStation(line, sectionRequest, newSection);
            return;
        }

        if (Section.isAddStation(sectionRequest.getDownStationId(), line.getUpStationId())) {
            addFirstStation(line, sectionRequest, newSection);
            return;
        }

        if (orderedSections.containsKey(sectionRequest.getUpStationId())) {
            addDownStation(orderedSections, line, sectionRequest);
            return;
        }

        if (reverseOrderedSections.containsKey(sectionRequest.getDownStationId())) {
            addUpStation(reverseOrderedSections, line, sectionRequest);
            return;
        }

        throw new IllegalArgumentException();
    }

    public void delete(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByStationIdAndLineId(stationId, lineId);
        Line line = lineDao.findById(lineId);

        if (line.isEndStation(sections.size())) {
            Section endSection = sections.get(0);
            line.updateEndStation(endSection, stationId);
            sectionDao.deleteById(endSection.getId());
            lineDao.update(line);
            return;
        }

        Section mergeSection = sections.get(0).merge(sections.get(1), stationId);
        sectionDao.deleteById(sections.get(1).getId());
        sectionDao.update(mergeSection);
    }

    public void validateCreate(SectionRequest sectionRequest, List<Station> stations) {
        if (hasDuplicatedStation(sectionRequest, stations)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if (!containsEndStation(sectionRequest, stations)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    public void validateDelete(Long lineId) {
        if (sectionDao.countByLineId(lineId) == 1) {
            throw new DeleteSectionException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음");
        }
    }

    private boolean hasDuplicatedStation(SectionRequest sectionRequest, List<Station> stations) {
        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                && stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    private boolean containsEndStation(SectionRequest sectionRequest, List<Station> stations) {
        return stations.contains(stationDao.findById(sectionRequest.getUpStationId()))
                || stations.contains(stationDao.findById(sectionRequest.getDownStationId()));
    }

    private void addLastStation(Line line, SectionRequest sectionRequest, Section newSection) {
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                line.getUpStationId(),
                sectionRequest.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);
        sectionDao.save(newSection);
    }

    private void addFirstStation(Line line, SectionRequest sectionRequest, Section newSection) {
        Line updateLine = new Line(line.getId(),
                line.getName(),
                line.getColor(),
                sectionRequest.getUpStationId(),
                line.getDownStationId(),
                line.getDistance() + sectionRequest.getDistance());

        lineDao.update(updateLine);
        sectionDao.save(newSection);
    }

    private void addDownStation(Map<Long, Section> orderedSections, Line line, SectionRequest sectionRequest) {
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

    private void addUpStation(Map<Long, Section> reverseOrderedSections, Line line, SectionRequest
            sectionRequest) {
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
}
