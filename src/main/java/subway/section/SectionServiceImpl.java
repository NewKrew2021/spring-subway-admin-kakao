package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineService;

import java.util.LinkedList;
import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionDao sectionDao;
    private final LineService lineService;

    public SectionServiceImpl(SectionDao sectionDao, LineService lineService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        List<Section> sections = sectionDao.getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);
        Long cur = line.getUpStationId();
        Long dest = line.getDownStationId();
        List<Section> orderedSections = new LinkedList();

        while (!cur.equals(dest)) {
            for (Section section : sections) {
                if (section.getUpStationId().equals(cur)) {
                    orderedSections.add(section);
                    cur = section.getDownStationId();
                    break;
                }
            }
        }
        return orderedSections;
    }

    public boolean deleteSectionById(Long sectionId) {
        return sectionDao.deleteSectionById(sectionId) != 0;
    }

    public boolean saveSection(Long lineId, Section section) {
        List<Section> sections = getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);
        int sectionIdx = -1;
        boolean upFlag = false, downFlag = false;
        Long upId = section.getUpStationId(), downId = section.getDownStationId(), stationId = -1L;
        for (int i = 0; i < sections.size(); i++) {
            if (upId == sections.get(i).getUpStationId() || upId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = upId;
                upFlag = true;
            }

            if (downId == sections.get(i).getUpStationId() || downId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = downId;
                downFlag = true;
            }
        }
        if (upFlag && downFlag) {
            return false;
        }
        if (sectionIdx == -1) {
            return false;
        }
        if (stationId == upId) {
            sections.add(sectionIdx, section);
            save(section);
            if (sectionIdx == sections.size() - 1) {
                lineService.update(new Line(line.getId(), line.getColor(), line.getName(), line.getUpStationId(), downId));
            }
            if (sectionIdx < sections.size() - 1) {
                Long nextStationId = sections.get(sectionIdx + 1).getDownStationId();
                Integer nextStationDistance = sections.get(sectionIdx + 1).getDistance() - section.getDistance();
                deleteSectionById(sections.get(sectionIdx + 1).getSectionId());
                sections.remove(sectionIdx + 1);
                sections.add(sectionIdx + 1, new Section(downId, nextStationId, nextStationDistance, lineId));
                save(new Section(downId, nextStationId, nextStationDistance, lineId));
            }
        }

        if (stationId == downId) {
            sections.add(sectionIdx, section);
            save(section);
            if (sectionIdx == 0) {
                lineService.update(new Line(line.getId(), line.getColor(), line.getName(), upId, line.getDownStationId()));
            }
            if (sectionIdx > 0) {
                Long prevStationId = sections.get(sectionIdx - 1).getDownStationId();
                Integer prevStationDistance = sections.get(sectionIdx - 1).getDistance() - section.getDistance();
                deleteSectionById(sections.get(sectionIdx - 1).getSectionId());
                sections.remove(sectionIdx - 1);
                sections.add(sectionIdx - 1, new Section(prevStationId, upId, prevStationDistance, lineId));
                save(new Section(prevStationId, upId, prevStationDistance, lineId));
            }
        }
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        List<Section> sections = getSectionsByLineId(lineId);

        if (sections.size() == 1) {
            return false;
        }
        int upIdx = -1, downIdx = -1;

        boolean upFlag = false, downFlag = false;
        for (int i = 0; i < sections.size(); i++) {
            if (stationId == sections.get(i).getUpStationId()) {
                upIdx = i;
                upFlag = true;
            }

            if (stationId == sections.get(i).getDownStationId()) {
                downIdx = i;
                downFlag = true;
            }
        }
        if (!upFlag && !downFlag) {
            return false;
        } else if (upFlag && downFlag) {
            Long nextStationId = sections.get(upIdx).getDownStationId();
            Integer nextDistance = sections.get(upIdx).getDistance();
            Long prevStationId = sections.get(downIdx).getUpStationId();
            Integer prevDistance = sections.get(downIdx).getDistance();

            deleteSectionById(sections.get(upIdx).getSectionId());
            sections.remove(upIdx);
            deleteSectionById(sections.get(downIdx).getSectionId());
            sections.remove(downIdx);

            sections.add(downIdx, new Section(prevStationId, nextStationId, nextDistance + prevDistance, lineId));
            save(new Section(prevStationId, nextStationId, nextDistance + prevDistance, lineId));
        } else if (upFlag) {
            deleteSectionById(sections.get(upIdx).getSectionId());

            sections.remove(upIdx);
        } else if (downFlag) {
            deleteSectionById(sections.get(downIdx).getSectionId());
            sections.remove(downIdx);

        }
        return true;
    }
}
