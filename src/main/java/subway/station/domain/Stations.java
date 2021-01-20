package subway.station.domain;

import subway.station.vo.StationResultValue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = Collections.unmodifiableList(stations);

        checkAreValidSections();
    }

    public List<StationResultValue> allToResultValues() {
        return stations.stream()
                .map(Station::toResultValue)
                .collect(Collectors.toList());
    }

    private void checkAreValidSections() {
        if (areInvalidNamesAndIDs()) {
            throw new IllegalArgumentException("There cannot be multiple stations with same name or ID");
        }
    }

    private boolean areInvalidNamesAndIDs() {
        return !(areValidNames() && areValidIDs());
    }

    private boolean areValidNames() {
        return stations.stream()
                .map(Station::getName)
                .distinct()
                .count() == stations.size();
    }

    private boolean areValidIDs() {
        return stations.stream()
                .map(Station::getID)
                .distinct()
                .count() == stations.size();
    }
}
