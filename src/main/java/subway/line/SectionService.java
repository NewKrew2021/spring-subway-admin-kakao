package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.station.Station;
import subway.station.StationDao;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private static final boolean LAST_SECTION = true;
    private static final boolean FIRST_SECTION = true;
    public static final boolean NOT_FIRST_SECTION = false;
    public static final boolean NOT_LAST_SECTION = false;

    @Resource
    private StationDao stationDao;
    @Resource
    private SectionDao sectionDao;

    public void create(Section section) {
        sectionDao.save(section);
    }

    public void create(Long lineId, Section section) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section firstSection = sectionDao.findFirstByLineId(lineId);
        Section lastSection = sectionDao.findLastByLineId(lineId);

        Map<Long, Section> orderedSections = sections.getOrderedSections();
        Map<Long, Section> reverseOrderedSections = sections.getReverseOrderedSections();

        if (section.getUpStationId().equals(lastSection.getDownStationId())) {
            addLastStation(section, lastSection);
            return;
        }

        if (section.getDownStationId().equals(firstSection.getUpStationId())) {
            addFirstStation(section, firstSection);
            return;
        }

        if (orderedSections.containsKey(section.getUpStationId())) {
            addDownStation(orderedSections, lineId, section);
            return;
        }

        if (reverseOrderedSections.containsKey(section.getDownStationId())) {
            addUpStation(reverseOrderedSections, lineId, section);
            return;
        }

        throw new IllegalArgumentException();
    }

    public void delete(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByStationIdAndLineId(stationId, lineId));
        Section firstSection = sectionDao.findFirstByLineId(lineId);
        Section lastSection = sectionDao.findLastByLineId(lineId);

        Map<Long, Section> orderedSections = sections.getOrderedSections();
        Map<Long, Section> reverseOrderedSections = sections.getReverseOrderedSections();

        if (stationId.equals(firstSection.getUpStationId())) {
            Section nextSection = orderedSections.get(firstSection.getDownStationId());
            nextSection.setFirstSection(true);
            sectionDao.deleteById(firstSection.getId());
            sectionDao.update(nextSection);
            return;
        }

        if (stationId.equals(lastSection.getDownStationId())) {
            Section previousSection = reverseOrderedSections.get(firstSection.getUpStationId());
            previousSection.setLastSection(true);
            sectionDao.deleteById(lastSection.getId());
            sectionDao.update(previousSection);
            return;
        }

        Section mergeSection = sections.getMergeSection(stationId);
        Section deleteSection = sections.getDeleteSection();
        sectionDao.deleteById(deleteSection.getId());
        sectionDao.update(mergeSection);
    }

    public void validateCreate(Section section, List<Station> stations) {
        if (hasDuplicatedStation(section, stations)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if (!containsEndStation(section, stations)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    public void validateDelete(Long lineId) {
        if (sectionDao.countByLineId(lineId) == 1) {
            throw new DeleteSectionException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음");
        }
    }

    private boolean hasDuplicatedStation(Section section, List<Station> stations) {
        return stations.contains(stationDao.findById(section.getUpStationId()))
                && stations.contains(stationDao.findById(section.getDownStationId()));
    }

    private boolean containsEndStation(Section section, List<Station> stations) {
        return stations.contains(stationDao.findById(section.getUpStationId()))
                || stations.contains(stationDao.findById(section.getDownStationId()));
    }

    private void addLastStation(Section section, Section lastSection) {
        lastSection.setLastSection(NOT_LAST_SECTION);
        section.setLastSection(LAST_SECTION);
        sectionDao.update(lastSection);
        sectionDao.save(section);
    }

    private void addFirstStation(Section section, Section firstSection) {
        firstSection.setFirstSection(NOT_FIRST_SECTION);
        section.setFirstSection(FIRST_SECTION);
        sectionDao.update(firstSection);
        sectionDao.save(section);
    }

    private void addDownStation(Map<Long, Section> orderedSections, Long lineId, Section sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        int distanceSum = 0;

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        lineId,
                        upStationId,
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance() - distanceSum,
                        section.isFirstSection(),
                        NOT_LAST_SECTION);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        lineId,
                        sectionRequest.getDownStationId(),
                        section.getDownStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance(),
                        NOT_FIRST_SECTION,
                        section.isLastSection());

                sectionDao.update(updateSection);
                return;
            }

            upStationId = section.getDownStationId();
            distanceSum += section.getDistance();
        }
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }

    private void addUpStation(Map<Long, Section> reverseOrderedSections, Long lineId, Section sectionRequest) {
        Long downStationId = sectionRequest.getDownStationId();
        int distanceSum = 0;

        while (reverseOrderedSections.containsKey(downStationId)) {
            Section section = reverseOrderedSections.get(downStationId);

            if (distanceSum + section.getDistance() == sectionRequest.getDistance()) {
                throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            if (distanceSum + section.getDistance() > sectionRequest.getDistance()) {
                Section newSection = new Section(
                        lineId,
                        sectionRequest.getUpStationId(),
                        downStationId,
                        sectionRequest.getDistance() - distanceSum,
                        NOT_FIRST_SECTION,
                        section.isLastSection());

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        lineId,
                        section.getUpStationId(),
                        sectionRequest.getUpStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance(),
                        section.isFirstSection(),
                        NOT_LAST_SECTION);

                sectionDao.update(updateSection);
                return;
            }

            downStationId = section.getUpStationId();
            distanceSum += section.getDistance();
        }
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }
}
