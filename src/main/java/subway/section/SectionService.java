package subway.section;

import org.springframework.stereotype.Service;
import subway.exception.InvalidSectionException;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

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
                .anyMatch(stationResponse -> stationResponse.getId().equals(section.getUpStationId()));
        boolean downStationExist = stations.stream()
                .anyMatch(stationResponse -> stationResponse.getId().equals(section.getDownStationId()));

        validateSection(upStationExist, downStationExist);

        if (upStationExist) {
            addSectionWhenUpStationExist(sectionRequest, line);
            return;
        }

        addSectionWhenDownStationExist(sectionRequest, line);
    }

    private void addSectionWhenUpStationExist(Section section, Line line) {
        if (section.getUpStationId().equals(line.getEndStationId())) {
            extendDownwardEdge(section, line);
            return;
        }

        Section existingSection = getSectionByUpstationId(section.getUpStationId());
        addSectionUpward(section, existingSection);
    }

    private void addSectionWhenDownStationExist(Section section, Line line) {
        if (section.getDownStationId().equals(line.getStartStationId())) {
            extendUpwardEdge(section, line);
            return;
        }

        Section existingSection = getSectionByDownstationId(sectionRequest.getDownStationId());
        validateDistance(sectionRequest.getDistance(), existingSection.getDistance());

        addSectionDownward(sectionRequest, existingSection);
    }

    private void extendDownwardEdge(Section section, Line line) {
        createSection(section);
        lineDao.updateById(line.getId(), line.getLineEndStationChanged(section.getDownStationId()));
    }

    private void extendUpwardEdge(Section section, Line line) {
        createSection(section);
        lineDao.updateById(line.getId(), line.getLineStartStationChanged(section.getUpStationId()));
    }

    private void addSectionUpward(Section section, Section existingSection) {
        updateSection(existingSection.getId(), existingSection.getSectionUpStationChanged(section));
        createSection(section);
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
