package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class LineService {
    private final SectionDao sectionDao;

    public void checkDuplicateName(Long lineId, Long upStationId, Long downStationId) {
        if (sectionDao.findOneByLineIdAndStationId(lineId, upStationId, true) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, upStationId, false) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, downStationId, true) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, downStationId, false) == null
        ) {
            throw new DuplicateNameException(upStationId + ", " + downStationId);
        }
    }

    public LineService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section addSection(Line line, Section section) {
        Sections sections = line.getSections();
        int targetIndex = -1;

        int upIndex = IntStream.range(1, sections.size())
                .filter(i -> sections.get(i).getUpStation().getId().equals(section.getUpStation().getId()))
                .findAny()
                .orElse(-1);

        int downIndex = IntStream.range(0, sections.size() - 1)
                .filter(i -> sections.get(i).getDownStation().getId().equals(section.getDownStation().getId()))
                .findAny()
                .orElse(-1);

        checkValidCondition(section, sections, upIndex, downIndex);

        targetIndex = getTargetIndexIfUpIndex(line, section, sections, targetIndex, upIndex);

        targetIndex = getTargetIndexIfDownIndex(line, section, sections, targetIndex, downIndex);

        return sections.get(targetIndex);
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
