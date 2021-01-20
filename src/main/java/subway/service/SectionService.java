package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.DeleteSectionException;
import subway.exception.SectionDistanceExceedException;
import subway.repository.SectionDao;
import subway.repository.StationDao;

import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final StationService stationService;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, StationDao stationDao, SectionDao sectionDao) {
        this.stationService = stationService;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void create(Section section) {
        sectionDao.save(section);
    }

    public void create(Long lineId, Section section) {
        validateCreate(section, stationService.getStations(lineId));

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

    public void delete(Long lineId, Long stationId) {
        validateDelete(lineId);

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section firstSection = sectionDao.findFirstByLineId(lineId);
        Section lastSection = sectionDao.findLastByLineId(lineId);

        Map<Long, Section> orderedSections = sections.getOrderedSections();
        Map<Long, Section> reverseOrderedSections = sections.getReverseOrderedSections();
        Sections containSections = sections.getContainSections(stationId);

        if (stationId.equals(firstSection.getUpStationId())) {
            Section nextSection = orderedSections.get(firstSection.getDownStationId());
            nextSection.setFirstSection(true);
            sectionDao.deleteById(firstSection.getId());
            sectionDao.update(nextSection);
            return;
        }

        if (stationId.equals(lastSection.getDownStationId())) {
            Section previousSection = reverseOrderedSections.get(lastSection.getUpStationId());
            previousSection.setLastSection(true);
            sectionDao.deleteById(lastSection.getId());
            sectionDao.update(previousSection);
            return;
        }

        Section mergeSection = containSections.getMergeSection(stationId);
        Section deleteSection = containSections.getDeleteSection();
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
        lastSection.setLastSection(Section.NOT_LAST_SECTION);
        section.setLastSection(Section.LAST_SECTION);
        sectionDao.update(lastSection);
        sectionDao.save(section);
    }

    private void addFirstStation(Section section, Section firstSection) {
        firstSection.setFirstSection(Section.NOT_FIRST_SECTION);
        section.setFirstSection(Section.FIRST_SECTION);
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
                        Section.NOT_LAST_SECTION);

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        lineId,
                        sectionRequest.getDownStationId(),
                        section.getDownStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance(),
                        Section.NOT_FIRST_SECTION,
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
                        Section.NOT_FIRST_SECTION,
                        section.isLastSection());

                sectionDao.save(newSection);

                Section updateSection = new Section(
                        section.getId(),
                        lineId,
                        section.getUpStationId(),
                        sectionRequest.getUpStationId(),
                        distanceSum + section.getDistance() - sectionRequest.getDistance(),
                        section.isFirstSection(),
                        Section.NOT_LAST_SECTION);

                sectionDao.update(updateSection);
                return;
            }

            downStationId = section.getUpStationId();
            distanceSum += section.getDistance();
        }
        throw new SectionDistanceExceedException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }
}
