package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import subway.domain.Line;
import subway.domain.Section;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Sections;
import subway.exception.NoContentException;

import java.util.stream.IntStream;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section getOneByLineIdAndStationId(@PathVariable Long lineId, @RequestParam("stationId") Long stationId, boolean b) {
        return sectionDao.findOneByLineIdAndStationId(lineId, stationId, b);
    }

    public Section getSection(Line line, Long upStationId, Long downStationId, int distance) {
        return new Section(line.getId(),
                stationDao.findOne(upStationId),
                stationDao.findOne(downStationId),
                distance);
    }

    public Section addSection(Line line, Section section) {
        Sections sections = sectionDao.findAll(line.getId());
        int targetIndex = -1;

        int upIndex = IntStream.range(1, sections.size())
                .filter(i -> isSameUpStation(sections, section, i))
                .findAny()
                .orElse(-1);

        int downIndex = IntStream.range(0, sections.size() - 1)
                .filter(i -> isSameDownStation(sections, section, i))
                .findAny()
                .orElse(-1);

        checkValidCondition(section, sections, upIndex, downIndex);

        targetIndex = getTargetIndexIfUpIndex(line, section, sections, targetIndex, upIndex);

        targetIndex = getTargetIndexIfDownIndex(line, section, sections, targetIndex, downIndex);

        return sections.get(targetIndex);
    }

    private boolean isSameUpStation(Sections sections, Section section, int index) {
        return sections.get(index).getUpStation().getId().equals(section.getUpStation().getId());
    }

    private boolean isSameDownStation(Sections sections, Section section, int index) {
        return sections.get(index).getDownStation().getId().equals(section.getDownStation().getId());
    }

    public Sections findAllSection(Line line) {
        return sectionDao.findAll(line.getId());
    }

    private int getTargetIndexIfDownIndex(Line line, Section section, Sections sections, int targetIndex, int downIndex) {
        if (downIndex != -1) {
            Section present = sections.get(downIndex);
            Section newSection = new Section(line.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
            Section updateSection = new Section(present.getId(), line.getId(), present.getUpStation(), section.getUpStation(), present.getDistance() - section.getDistance());
            updateSectionIfRealSection(present, updateSection);
            sectionDao.save(newSection);
            targetIndex = downIndex + 1;
        }
        return targetIndex;
    }

    private int getTargetIndexIfUpIndex(Line line, Section section, Sections sections, int targetIndex, int upIndex) {
        if (upIndex != -1) {
            Section present = sections.get(upIndex);
            Section newSection = new Section(line.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
            Section updateSection = new Section(present.getId(), line.getId(), section.getDownStation(), present.getDownStation(), present.getDistance() - section.getDistance());
            updateSectionIfRealSection(present, updateSection);
            sectionDao.save(newSection);
            targetIndex = upIndex;
        }
        return targetIndex;
    }

    private void checkValidCondition(Section section, Sections sections, int upIndex, int downIndex) {
        if ((upIndex == -1) == (downIndex == -1)) {
            throw new NoContentException("둘 중 하나만 -1이여야함!");
        }

        if (section.getDistance() >= sections.get(upIndex * downIndex * -1).getDistance()) {
            throw new NoContentException("길이가 맞지 않음");
        }
    }

    private void updateSectionIfRealSection(Section present, Section updateSection) {
        if (present.getId() != null) {
            sectionDao.update(updateSection);
        }
    }
}
