package subway.section;

import subway.exception.InvalidSectionException;
import subway.line.Line;
import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

import static subway.Container.*;

public class SectionService {

    public void createSection(Section section) {
        sectionDao.save(section);
    }

    public List<StationResponse> getStationsOfLine(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        Station curStation = stationDao.findById(line.getStartStationId());
        Section curSection = sectionDao.findByUpStationId(curStation.getId());
        while (curSection != null) {
            stations.add(new StationResponse(curStation.getId(), curStation.getName()));
            curStation = stationDao.findById(curSection.getDownStationId());
            curSection = sectionDao.findByUpStationId(curStation.getId());
        }
        stations.add(new StationResponse(curStation.getId(), curStation.getName()));
        return stations;
    }

    public Section getSectionByUpstationId(Long id) {
        return sectionDao.findByUpStationId(id);
    }

    public Section getSectionByDownstationId(Long id) {
        return sectionDao.findByDownStationId(id);
    }

    public void updateSection(long id, Section section) {
        sectionDao.updateSection(id, section);
    }

    public void addSection(long id, SectionRequest sectionRequest) {
        Line line = lineDao.findById(id);
        List<StationResponse> stations = getStationsOfLine(line);

        boolean upStationExist = stations.stream()
                .anyMatch(stationResponse -> stationResponse.getId().equals(sectionRequest.getUpStationId()));
        boolean downStationExist = stations.stream()
                .anyMatch(stationResponse -> stationResponse.getId().equals(sectionRequest.getDownStationId()));

        validateSection(upStationExist, downStationExist);

        if (upStationExist) {
            addSectionWhenUpStationExist(sectionRequest, line);
            return;
        }

        addSectionWhenDownStationExist(sectionRequest, line);
    }

    public void deleteSection(long lineId, long stationId) {
        Line line = lineDao.findById(lineId);
        if (stationId == line.getStartStationId()) {
            deleteStartStation(line, stationId);
            return;
        }
        if (stationId == line.getEndStationId()) {
            deleteEndStation(line, stationId);
            return;
        }
        deleteMiddleStation(line, stationId);
    }

    private void deleteMiddleStation(Line line, long stationId) {
        Section upSection = sectionDao.findByDownStationId(stationId);
        Section downSection = sectionDao.findByUpStationId(stationId);
        sectionDao.updateSection(upSection.getId(), new Section(upSection.getId(),
                upSection.getUpStationId(),
                downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance(),
                line.getId()));
        sectionDao.deleteById(downSection.getId());
    }

    private void deleteStartStation(Line line, long stationId) {
        Section section = sectionDao.findByUpStationId(stationId);
        if (section.getDownStationId().equals(line.getEndStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), new Line(line.getId(), line.getName(), line.getColor(), section.getDownStationId(), line.getEndStationId()));
    }

    private void deleteEndStation(Line line, long stationId) {
        Section section = sectionDao.findByDownStationId(stationId);
        if (section.getUpStationId().equals(line.getStartStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), new Line(line.getId(), line.getName(), line.getColor(), line.getStartStationId(), section.getUpStationId()));
    }

    private void extendDownwardEdge(SectionRequest sectionRequest, Line line) {
        createSection(new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), line.getId()));
        lineDao.updateById(line.getId(), new Line(line.getName(), line.getColor(), line.getStartStationId(), sectionRequest.getDownStationId()));
    }

    private void extendUpwardEdge(SectionRequest sectionRequest, Line line) {
        createSection(new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), line.getId()));
        lineDao.updateById(line.getId(), new Line(line.getName(), line.getColor(), sectionRequest.getUpStationId(), line.getEndStationId()));
    }

    private void addSectionWhenUpStationExist(SectionRequest sectionRequest, Line line) {
        if (sectionRequest.getUpStationId().equals(line.getEndStationId())) {
            extendDownwardEdge(sectionRequest, line);
            return;
        }

        Section existingSection = getSectionByUpstationId(sectionRequest.getUpStationId());
        validateDistance(sectionRequest.getDistance(), existingSection.getDistance());

        addSectionUpward(sectionRequest, existingSection);
    }

    private void addSectionWhenDownStationExist(SectionRequest sectionRequest, Line line) {
        if (sectionRequest.getDownStationId().equals(line.getStartStationId())) {
            extendUpwardEdge(sectionRequest, line);
            return;
        }

        Section existingSection = getSectionByDownstationId(sectionRequest.getDownStationId());
        validateDistance(sectionRequest.getDistance(), existingSection.getDistance());

        addSectionDownward(sectionRequest, existingSection);
    }

    private void validateDistance(int newDistance, int existingDistance) {
        if (newDistance >= existingDistance) {
            throw new InvalidSectionException("기존 구간보다 거리가 크거나 같습니다.");
        }
    }

    private void addSectionUpward(SectionRequest sectionRequest, Section existingSection) {
        updateSection(existingSection.getId(), new Section(existingSection.getId(),
                sectionRequest.getDownStationId(), existingSection.getDownStationId(),
                existingSection.getDistance() - sectionRequest.getDistance(),
                existingSection.getLineId()));

        Section newSection = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), existingSection.getLineId());
        createSection(newSection);
    }

    private void addSectionDownward(SectionRequest sectionRequest, Section existingSection) {
        updateSection(existingSection.getId(), new Section(existingSection.getId(),
                existingSection.getUpStationId(), sectionRequest.getUpStationId(),
                existingSection.getDistance() - sectionRequest.getDistance(),
                existingSection.getLineId()));

        Section newSection = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), existingSection.getLineId());
        createSection(newSection);
    }

    private void validateSection(boolean upStationExist, boolean downStationExist) {
        if (upStationExist && downStationExist) {
            throw new InvalidSectionException("등록하려는 노선이 이미 존재합니다.");
        }
        if (!upStationExist && !downStationExist) {
            throw new InvalidSectionException("이어진 노선이 존재하지 않습니다.");
        }
    }
}
