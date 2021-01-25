package subway.domain.line;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class LineTest {
    private static final Long LINE_ID = 1L;
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 광교역 = new Station(3L, "광교역");
    private static final Station 망포역 = new Station(4L, "망포역");
    private static final Station 수원역 = new Station(5L, "수원역");

    private static final Section 강남_역삼 = new Section(1L, LINE_ID, 강남역, 역삼역, 5);
    private static final Section 역삼_광교 = new Section(2L, LINE_ID, 역삼역, 광교역, 5);

    private static Sections sections;
    private static Line line;

    @BeforeAll
    public static void setUp() {
        sections = new Sections(Arrays.asList(강남_역삼, 역삼_광교));
        line = new Line(LINE_ID, "수인선", "red", sections);
    }

    @DisplayName("line에 속하는 station 테스트")
    @ParameterizedTest
    @MethodSource("provideStationInLine")
    public void getAllStations(Station station) {
        assertThat(line.getAllStations().contain(station.getId())).isTrue();
    }

    private static Stream<Arguments> provideStationInLine() {
        return Stream.of(
                Arguments.of(강남역),
                Arguments.of(역삼역),
                Arguments.of(광교역)
        );
    }

    @DisplayName("line에 속하지 않는 station 테스트")
    @ParameterizedTest
    @MethodSource("provideStationOutLine")
    public void stationOutOfLine(Station station) {
        assertThat(line.getAllStations().contain(station.getId())).isFalse();
    }

    private static Stream<Arguments> provideStationOutLine() {
        return Stream.of(
                Arguments.of(망포역),
                Arguments.of(수원역)
        );
    }
}
