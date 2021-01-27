package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public void updateSection(long id, Section section) {
        sectionDao.updateById(id, section);
    }

    @Transactional
    public void addSection(long id, Section section) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }

        List<Long> stationIds = line.getSections().getStationIds();

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

        Section existingSection = line.getSections().findByUpStationId(section.getUpStationId());
        addSectionUpward(section, existingSection);
    }

    private void addSectionWhenDownStationExist(Section section, Line line) {
        if (section.getDownStationId().equals(line.getStartStationId())) {
            extendUpwardEdge(section, line);
            return;
        }

        Section existingSection = line.getSections().findByDownStationId(section.getDownStationId());
        addSectionDownward(section, existingSection);
    }

    private void extendDownwardEdge(Section section, Line line) {
        createSection(section);
        if (lineDao.updateById(line.getId(), line.getLineEndStationChanged(section.getDownStationId())) == 0) {
            throw new InvalidSectionException("구간 생성 중 에러가 발생했습니다.");
        }
    }

    private void extendUpwardEdge(Section section, Line line) {
        createSection(section);
        if (lineDao.updateById(line.getId(), line.getLineStartStationChanged(section.getUpStationId())) == 0) {
            throw new InvalidSectionException("구간 생성 중 에러가 발생했습니다.");
        }
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
        Section upSection = line.getSections().findByDownStationId(stationId);
        Section downSection = line.getSections().findByUpStationId(stationId);
        sectionDao.updateById(upSection.getId(), upSection.getMergedSection(downSection));
        sectionDao.deleteById(downSection.getId());
    }

    private void deleteStartStation(Line line, long stationId) {
        Section section = line.getSections().findByUpStationId(stationId);
        if (line.isEndStation(section.getDownStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), line.getLineStartStationChanged(section.getDownStationId()));
    }

    private void deleteEndStation(Line line, long stationId) {
        Section section = line.getSections().findByDownStationId(stationId);
        if (line.isStartStation(section.getUpStationId())) {
            throw new InvalidSectionException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
        sectionDao.deleteById(section.getId());
        lineDao.updateById(line.getId(), line.getLineEndStationChanged(section.getUpStationId()));
    }
}
