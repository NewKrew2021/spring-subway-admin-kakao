package subway.section;

import subway.line.Line;
import subway.line.LineInfoChanged;
import subway.line.LineInfoChangedResult;

import java.util.*;

public class Sections {
    List<Section> sections;
    List<Section> addSections = new ArrayList<>();
    List<Section> delSections = new ArrayList<>();

    public static final Long NOT_EXIST = -1L;

    Sections() {
        sections = new ArrayList<>();
    }

    Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public Section findSectionByUpStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByDownStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id) || sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Long findStationExistBySection(Section section) {
        for (Section sec : sections) {
            if (sec.getUpStationId().equals(section.getUpStationId()) ||
                    sec.getDownStationId().equals(section.getUpStationId())) {
                return section.getUpStationId();
            }
            if (sec.getUpStationId().equals(section.getDownStationId()) ||
                    sec.getDownStationId().equals(section.getDownStationId())) {
                return section.getDownStationId();
            }
        }

        return NOT_EXIST;
    }

    public boolean isPossibleToDelete(Long stationId) {
        return findSectionByStationId(stationId) != null && sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Sections getOrderedSection(Line line) {
        Long cur = line.getUpStationId();
        Long dest = line.getDownStationId();
        List<Section> orderedSections = new LinkedList();
        Set<Long> visit = new HashSet<>();

        while (!cur.equals(dest) && visit.add(cur)) {
            Section section = findSectionByUpStationId(cur);
            orderedSections.add(section);
            cur = section.getDownStationId();
        }

        if(visit.contains(cur))
            throw new RuntimeException("반복되는 구간이 존재합니다.");

        return new Sections(orderedSections);
    }

    public boolean checkSectionValidation(Section section){
        if (isContainingSameSection(section) || findStationExistBySection(section) == Sections.NOT_EXIST) {
            return false;
        }
        return true;
    }

    public boolean isContainingSameSection(Section section) {
        return sections.contains(section);
    }
    //판교 -       강남 - 광교
    //      정자 - 강남
    public Section findNextSection(Section section){
        return findSectionByUpStationId(section.getUpStationId());
    }

    public Section findPrevSection(Section section){
        return findSectionByDownStationId(section.getDownStationId());
    }

    public LineInfoChangedResult addSection(Line line, Section section) {
        Long existStationId = findStationExistBySection(section);

        if(!checkSectionValidation(section))
            throw new IllegalArgumentException("적절한 구간 정보를 입력해주세요.");

        addSections.add(section);

        if (section.getUpStationId() == existStationId) {
            return addSectionBack(line, section);
        }

        return addSectionFront(line, section);
    }

    public List<Section> getAddSections() {
        return addSections;
    }

    public List<Section> getDelSections() {
        return delSections;
    }

    private LineInfoChangedResult addSectionBack(Line line, Section section) {
        Section nextSection = findNextSection(section);
        if (line.isFinalDownStation(section) || nextSection == null) {
            return new LineInfoChangedResult(LineInfoChanged.DOWN_STATION_CHANGED, line.getId(), section.getDownStationId());
        }
        delSections.add(nextSection);
        addSections.add(new Section(section.getDownStationId(),
                nextSection.getDownStationId(),
                nextSection.getDistance() - section.getDistance(),
                line.getId()));

        return new LineInfoChangedResult(LineInfoChanged.NONE);
    }

    private LineInfoChangedResult addSectionFront(Line line, Section section) {
        Section prevSection = findPrevSection(section);
        if (line.isFinalUpStation(section) || prevSection == null) {
            return new LineInfoChangedResult(LineInfoChanged.UP_STATION_CHANGED, line.getId(), section.getUpStationId());
        }

        delSections.add(prevSection);
        addSections.add(new Section(prevSection.getUpStationId(),
                section.getUpStationId(),
                prevSection.getDistance() - section.getDistance(),
                line.getId()));

        return new LineInfoChangedResult(LineInfoChanged.NONE);
    }

    public void initSectionsBeforeHandling(){
        addSections.clear();
        delSections.clear();
    }

    public LineInfoChangedResult deleteStation(Long lineId, Long stationId) {
        if (!isPossibleToDelete(stationId)) {
            throw new IllegalArgumentException("적절하지 않은 역 정보입니다.");
        }

        Section nextSection = findSectionByUpStationId(stationId);
        Section prevSection = findSectionByDownStationId(stationId);

        if (nextSection != null && prevSection != null) {
            delSections.add(nextSection);
            delSections.add(prevSection);
            addSections.add(new Section(prevSection.getUpStationId(),
                    nextSection.getDownStationId(),
                    prevSection.getDistance() + nextSection.getDistance(),
                    lineId));
        } else if (nextSection != null) {
            delSections.add(nextSection);
            return new LineInfoChangedResult(LineInfoChanged.UP_STATION_CHANGED, lineId, nextSection.getDownStationId());
        } else if (prevSection != null) {
            delSections.add(prevSection);
            return new LineInfoChangedResult(LineInfoChanged.DOWN_STATION_CHANGED, lineId, prevSection.getUpStationId());
        }

        return new LineInfoChangedResult(LineInfoChanged.NONE);
    }
}
