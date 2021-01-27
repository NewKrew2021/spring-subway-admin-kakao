package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineService;

@Service
public class SectionService {
    private SectionDao sectionDao;
    private LineService lineService;

    public SectionService(SectionDao sectionDao, LineService lineService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
    }

    public void insertNewSection(Section section) {
        Line line = lineService.getLineById(section.getLineId());
        boolean isUpdated = false;

        isUpdated = updateLineFront(section, line) || isUpdated;
        isUpdated = updateLineBack(section, line) || isUpdated;
        isUpdated = updateMatchUpToUp(section, line) || isUpdated;
        isUpdated = updateMatchDownToDown(section, line) || isUpdated;

        if(!isUpdated){
            throw new IllegalArgumentException("상행역과 하행역 모두 노선에 등록되어 있지 않아 구간을 추가할 수 없습니다.");
        }

        sectionDao.insert(section);
    }

    private boolean updateLineFront(Section section, Line line){
        if (line.getUpStationId() == section.getDownStationId()) { // 상행 종점으로 삽입
            lineService.updateLine(new Line(line.getId(), line.getName(), line.getColor(), section.getUpStationId(), line.getDownStationId()));
            return true;
        }
        return false;
    }

    private boolean updateLineBack(Section section, Line line){
        if (line.getDownStationId() == section.getUpStationId()) { // 하행 종점으로 삽입
            lineService.updateLine(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), section.getDownStationId()));
            return true;
        }
        return false;
    }

    private boolean updateMatchUpToUp(Section section, Line line) {
        if (sectionDao.existByLineIdAndUpStationId(line.getId(), section.getUpStationId())) { // 상행역이 일치하는 경우 새로운 구간 중간 삽입
            Section currentSection = sectionDao.findByLineIdAndUpStationId(line.getId(), section.getUpStationId());
            int newDistance = checkValidAndGetDistance(section, currentSection);
            sectionDao.update(new Section(currentSection.getId(), section.getLineId(), section.getDownStationId(), currentSection.getDownStationId(), newDistance));
            return true;
        }
        return false;
    }

    private boolean updateMatchDownToDown(Section section, Line line) {
        if (sectionDao.existByLineIdAndDownStationId(line.getId(), section.getDownStationId())) { // 하행역이 일치하는 경우 새로운 구간 중간 삽입
            Section currentSection = sectionDao.findByLineIdAndDownStationId(line.getId(), section.getDownStationId());
            int newDistance = checkValidAndGetDistance(section, currentSection);
            sectionDao.update(new Section(currentSection.getId(), section.getLineId(), currentSection.getUpStationId(), section.getUpStationId(), newDistance));
            return true;
        }
        return false;
    }

    private int checkValidAndGetDistance(Section section, Section currentSection){
        section.checkValidInsert(currentSection);
        return currentSection.getDistance() - section.getDistance();
    }

    public int deleteSection(Long lineId, Long stationId) {
        if (sectionDao.countByLineId(lineId) < 2) {
            throw new IllegalArgumentException("구간이 하나 이하인 노선에서는 구간을 제거할 수 없습니다.");
        }

        Line line = lineService.getLineById(lineId);
        if (line.getUpStationId() == stationId) { // 상행 종점 제거
            Section currentSection = sectionDao.findByLineIdAndUpStationId(lineId, stationId);
            lineService.updateLine(new Line(lineId, line.getName(), line.getColor(), currentSection.getDownStationId(), line.getDownStationId(), line.getDistance()));
            return sectionDao.delete(currentSection);
        }
        if (line.getDownStationId() == stationId) { // 하행 종점 제거
            Section currentSection = sectionDao.findByLineIdAndDownStationId(lineId, stationId);
            lineService.updateLine(new Line(lineId, line.getName(), line.getColor(), line.getUpStationId(), currentSection.getUpStationId(), line.getDistance()));
            return sectionDao.delete(currentSection);
        }
        // 중간 구간 제거
        int deleteCount = 0;
        Section upSection = sectionDao.findByLineIdAndDownStationId(lineId, stationId);
        Section downSection = sectionDao.findByLineIdAndUpStationId(lineId, stationId);
        int newDistance = upSection.getDistance() + downSection.getDistance();
        Section newSection = new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(), newDistance);
        sectionDao.insert(newSection);
        deleteCount += sectionDao.delete(upSection);
        deleteCount += sectionDao.delete(downSection);
        return deleteCount;
    }

    public void createSection(Section section) {
        sectionDao.insert(section);
    }
}
