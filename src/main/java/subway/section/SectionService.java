package subway.section;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.StationNotFoundException;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(Sections sections) {
        for (Section section : sections.getSections()) {
            sectionDao.save(section);
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findStationsOf(long lineId) {
        return findSectionsBy(lineId)
                .getSortedStationIds()
                .stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new StationNotFoundException(stationId)))
                .map(StationResponse::from)
                .collect(toList());
    }

    public void createSection(long lineId, SectionRequest request) {
        Section newSection = request.toEntity(lineId);
        Section insertTargetSection = findSectionsBy(lineId)
                .findBySameUpOrDownStationWith(newSection);
        Section residualSection = insertTargetSection.subtractWith(newSection);

        sectionDao.delete(insertTargetSection);
        sectionDao.save(newSection);
        sectionDao.save(residualSection);
    }

    public void removeSection(long lineId, long stationId) {
        Sections sections = findSectionsBy(lineId);
        if (sections.isInitialState()) {
            throw new IllegalStateException("해당 노선은 지하철역을 삭제할 수 없습니다.");
        }

        Pair<Section, Section> connectedSections = sections.findByStationId(stationId);
        Section first = connectedSections.getLeft();
        Section second = connectedSections.getRight();
        Section joinedSection = first.mergeWith(second);

        sectionDao.delete(first);
        sectionDao.delete(second);
        sectionDao.save(joinedSection);
    }

    private Sections findSectionsBy(long lineId) {
        return new Sections(sectionDao.findByLineId(lineId));
    }
}
