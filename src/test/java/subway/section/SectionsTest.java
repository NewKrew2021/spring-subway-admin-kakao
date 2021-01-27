package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private static final int DISTANCE = 2;
    private static final long LINE_ID = 1;

    @Test
    @DisplayName("구간을 순서대로 정렬한 후 정렬된 역 ID 리스트를 반환한다.")
    void getSortedStationIds() {
        Sections sections = Sections.of(List.of(
                new Section(9L, 8L, DISTANCE, LINE_ID),
                new Section(1L, 4L, DISTANCE, LINE_ID),
                new Section(3L, 9L, DISTANCE, LINE_ID),
                new Section(4L, 3L, DISTANCE, LINE_ID)
        ), 1L);

        assertThat(sections.getStationIds()).containsExactly(1L, 4L, 3L, 9L, 8L);
    }
}
