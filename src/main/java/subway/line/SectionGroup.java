package subway.line;

import subway.exception.NoContentException;
import subway.exception.TwoStationException;
import subway.station.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SectionGroup {

    private final List<Section> sections;

    public SectionGroup() {
        sections = new ArrayList<>();
    }

    public Section insertFirstSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(null, upStation, Integer.MAX_VALUE));
        sections.add(new Section(upStation, downStation, distance));
        sections.add(new Section(downStation, null, Integer.MAX_VALUE));
        return sections.get(1);
    }

    public Section insertSection(Station upStation, Station downStation, int distance) {
        int insertedIndex = findInsertedSectionIndex(upStation, downStation);

        if (distance >= sections.get(insertedIndex).getDistance()) {
            throw new IllegalArgumentException("기존 노선보다 작은 길이를 입력해야 합니다.");
        }

        Section insertedSection = new Section(upStation, downStation, distance);
        divideSection(insertedSection, insertedIndex);
        return insertedSection;
    }

    private int findInsertedSectionIndex(Station upStation, Station downStation) {
        int upIndex = findSectionIndexWithUpStation(upStation).orElse(-1);
        int downIndex = findSectionIndexWithDownStation(downStation).orElse(-1);

        if ((upIndex == -1) == (downIndex == -1)) {
            throw new IllegalArgumentException("두 역이 모두 없거나 있으면 안됩니다.");
        }

        return upIndex * downIndex * (-1);
    }

    private void divideSection(Section insertedSection, int insertedIndex) {
        Section present = sections.get(insertedIndex);
        int dividedDistance = present.getDistance() - insertedSection.getDistance();

        if (present.shareUpStation(insertedSection)) {
            sections.set(insertedIndex, new Section(insertedSection.getDownStation(), present.getDownStation(), dividedDistance));
        }

        if (present.shareDownStation(insertedSection)) {
            sections.set(insertedIndex, new Section(present.getUpStation(), insertedSection.getUpStation(), dividedDistance));
            insertedIndex += 1;
        }

        sections.add(insertedIndex, insertedSection);
    }

    public void deleteStation(Station station) {
        if (sections.size() <= 3) {
            throw new TwoStationException();
        }
        int deletedIndex = findSectionIndexWithUpStation(station)
                .orElseThrow(() -> new NoContentException("삭제하려는 역이 없습니다."));

        Station upBound = sections.get(deletedIndex - 1).getUpStation();
        Station downBound = sections.get(deletedIndex).getDownStation();
        int combinedDistance = sections.get(deletedIndex - 1).getDistance() + sections.get(deletedIndex).getDistance();

        sections.set(deletedIndex - 1, new Section(upBound, downBound, combinedDistance));
        sections.remove(deletedIndex);
    }

    private OptionalInt findSectionIndexWithUpStation(Station station) {
        return IntStream.range(1, sections.size())
                .filter(i -> sections.get(i).isUpStation(station))
                .findAny();
    }

    private OptionalInt findSectionIndexWithDownStation(Station station) {
        return IntStream.range(0, sections.size() - 1)
                .filter(i -> sections.get(i).isDownStation(station))
                .findAny();
    }

    public List<Station> getStations() {
        return sections.stream()
                .skip(1)
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
