package subway.section.vo;

import subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SectionResultValues {
    private final List<SectionResultValue> values;

    public SectionResultValues(List<SectionResultValue> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public List<StationResponse> toResponses() {
        return values.stream()
                .map(SectionResultValue::toStationResponse)
                .collect(Collectors.toList());
    }
}
