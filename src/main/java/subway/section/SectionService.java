package subway.section;

import org.springframework.stereotype.Service;
import subway.exceptions.NotFoundException;
import subway.station.Station;
import subway.station.StationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, SectionDto sectionDto) {
        Section createdSection = new Section(lineId, sectionDto);
        sectionDao.save(createdSection);
    }

    public void save(Section newSection) {
        sectionDao.save(newSection);
    }

    public List<Station> findSortedStationsByLineId(Long lineId) { //TODO : 뭔가 이 메서드 리팩토링이 필요하다.
        /* 주어진 Line 위에 정의된 모든 section들을 collect */
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        /* 현재 station에서 다음 station을 참조할 수 있는 map을 생성 */
        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for(Section section : sections) {
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        /* 정렬된 station이 저장될 곳 */
        List<Station> stations = new ArrayList<>();

        /* 일렬로 탐색하면서 station을 수집한다. */
        Long currentId = getFirstStationId(lineId);
        while(currentId != null) {
            stations.add(stationService.findById(currentId));
            currentId = upStationToDownStation.get(currentId);
        }

        return stations;
    }

    private Long getFirstStationId(Long id) {
        List<Section> sections = sectionDao.findAllByLineId(id);

        Map<Long, Boolean> isStartPoint = new HashMap<>();

        /* 모든 station에 대해 start point = true 라는 의미로 초기화 */
        for(Section section : sections) {
            isStartPoint.put(section.getUpStationId(), true);
            isStartPoint.put(section.getDownStationId(), true);
        }

        /* 모든 section의 downStation Id에 대해 false 처리함 */
        for(Section section : sections) {
            isStartPoint.put(section.getDownStationId(), false);
        }

        /* downStation으로 등장하지 않은 한개의 노드(시작점) 을 return */
        return isStartPoint.keySet().stream()
                .filter(key -> isStartPoint.get(key))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("first station을 찾는데 실패했습니다."));
    }

    public void deleteStation(Long lineId, Long stationId) {
        sectionDao.deleteStation(lineId, stationId);
    }
}
