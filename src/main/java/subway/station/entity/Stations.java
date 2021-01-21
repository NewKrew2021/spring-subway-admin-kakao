package subway.station.entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        validate(stations);
        this.stations = Collections.unmodifiableList(stations);
    }

    private void validate(List<Station> stations) {
        if (isEmpty(stations)) {
            throw new IllegalArgumentException("비어있는 지하철 역 리스트를 입력받을 수 없습니다.");
        }
        if (hasDuplicateName(stations)) {
            throw new IllegalArgumentException("동일한 이름을 가진 지하철 역을 입력받을 수 없습니다.");
        }
    }

    private boolean isEmpty(List<Station> stations) {
        return stations == null || stations.isEmpty();
    }

    private boolean hasDuplicateName(List<Station> stations) {
        return stations.stream()
                .map(Station::getName)
                .distinct()
                .count() != stations.size();
    }

    public Stream<Station> stream() {
        return stations.stream();
    }
}
