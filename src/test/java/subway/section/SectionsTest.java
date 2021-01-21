package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private static final int DISTANCE = 2;
    private static final long LINE_ID = 1;

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(
                new Section(9L, 8L, DISTANCE, LINE_ID),
                new Section(1L, 4L, DISTANCE, LINE_ID),
                new Section(3L, 9L, DISTANCE, LINE_ID),
                new Section(4L, 3L, DISTANCE, LINE_ID)
        ));
    }

    @ParameterizedTest
    @MethodSource("provideStationIds")
    @DisplayName("구간을 순서대로 정렬한 후 정렬된 역 ID 리스트를 반환한다.")
    void getSortedStationIds(long startStationId, List<Long> order) {
        // when
        List<Long> stationIds = sections.getSortedStationIds(startStationId);

        //then
        assertThat(stationIds).containsExactlyElementsOf(order);
    }

    static Stream<Arguments> provideStationIds() {
        return Stream.of(
                Arguments.of(1L, List.of(1L, 4L, 3L, 9L, 8L)),
                Arguments.of(4L, List.of(4L, 3L, 9L, 8L)),
                Arguments.of(3L, List.of(3L, 9L, 8L)),
                Arguments.of(9L, List.of(9L, 8L))
        );
    }
}
