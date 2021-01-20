package subway.section;

import org.springframework.stereotype.Service;
import subway.exception.InvalidSectionException;
import subway.exception.NotExistException;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.StationDao;

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

    private void validateSection(boolean upStationExist, boolean downStationExist) {
        if (upStationExist && downStationExist) {
            throw new InvalidSectionException("등록하려는 노선이 이미 존재합니다.");
        }
        if (!upStationExist && !downStationExist) {
            throw new InvalidSectionException("이어진 노선이 존재하지 않습니다.");
        }
    }

    public Section createSection(Section section) {
        if (stationDao.countById(section.getUpStationId()) == 0) {
            throw new NotExistException("상행역이 존재하지 않습니다.");
        }
        if (stationDao.countById(section.getDownStationId()) == 0) {
            throw new NotExistException("하행역이 존재하지 않습니다.");
        }
        return sectionDao.save(section);
    }

    public List<Long> getStationIdsOfLine(Line line) {
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));
        return sections.getSortedStationIds(line.getStartStationId());
    }

    public void updateSection(long id, Section section) {
        sectionDao.updateSection(id, section);
    }

    public void addSection(long id, Section section) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }

        List<Long> stationIds = getStationIdsOfLine(line);

        boolean upStationExist = stationIds.stream()
                .anyMatch(section.getUpStationId()::equals);
        boolean downStationExist = stationIds.stream()
                .anyMatch(section.getDownStationId()::equals);

        validateSection(upStationExist, downStationExist);

        if (upStationExist) {
            addSectionWhenUpStationExist(section, line);
            return;
        }

        addSectionWhenDownStationExist(section, line);
    }

    private void addSectionWhenUpStationExist(Section section, Line line) {
        if (section.getUpStationId().equals(line.getEndStationId())) {
            extendDownwardEdge(section, line);
            return;
        }

        Section existingSection = getSectionByUpstationIdAndLineId(section.getUpStationId(), line.getId());
        addSectionUpward(section, existingSection);
    }

    private void addSectionWhenDownStationExist(Section section, Line line) {
        if (section.getDownStationId().equals(line.getStartStationId())) {
            extendUpwardEdge(section, line);
            return;
        }

        Section existingSection = getSectionByDownstationIdAndLineId(section.getDownStationId(), line.getId());
        addSectionDownward(section, existingSection);
    }

    public Section getSectionByUpstationIdAndLineId(Long upStationId, Long lineId) {
        return sectionDao.findByUpStationIdAndLineId(upStationId, lineId);
    }

    public Section getSectionByDownstationIdAndLineId(Long downStationId, Long lineId) {
        return sectionDao.findByDownStationIdAndLineId(downStationId, lineId);
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

    private void addSectionDownward(Section section, Section existingSection) {
        updateSection(existingSection.getId(), existingSection.getSectionDownStationChanged(section));
        createSection(section);
    }

    public void deleteSection(long lineId, long stationId) {
        Line line = lineDao.findById(lineId);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
        if (stationDao.countById(stationId) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }

        if (line.isStartStation(stationId)) {
            deleteStartStation(line, stationId);
            return;
        }
        if (line.isEndStation(stationId)) {
            deleteEndStation(line, stationId);
            return;
        }
        deleteMiddleStation(line, stationId);
    }

    private void deleteMiddleStation(Line line, long stationId) {
        Section upSection = sectionDao.findByDownStationIdAndLineId(stationId, line.getId());
        Section downSection = sectionDao.findByUpStationIdAndLineId(stationId, line.getId());
        sectionDao.updateSection(upSection.getId(), upSection.getMergedSection(downSection));
        sectionDao.deleteById(downSection.getId());
    }

    private void deleteStartStation(Line line, long stationId) {
        Section section = sectionDao.findByUpStationIdAndLineId(stationId, line.getId());
        if (line.isEndStation(section.getDownStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), line.getLineStartStationChanged(section.getDownStationId()));
    }

    private void deleteEndStation(Line line, long stationId) {
        Section section = sectionDao.findByDownStationIdAndLineId(stationId, line.getId());
        if (line.isStartStation(section.getUpStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), line.getLineEndStationChanged(section.getUpStationId()));
    }
}
