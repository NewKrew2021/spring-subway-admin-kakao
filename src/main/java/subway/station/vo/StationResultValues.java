package subway.station.vo;

import subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StationResultValues {
    private final List<StationResultValue> values;

    public StationResultValues() {
        this.values = Collections.emptyList();
    }

    public StationResultValues(List<StationResultValue> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public List<StationResponse> allToResponses() {
        return values.stream()
                .map(StationResultValue::toResponse)
                .collect(Collectors.toList());
    }
}
