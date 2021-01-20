package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상행부터 하행 순으로 나열된 구간 테스트")
public class OrderedSectionsTest {
    private OrderedSections orderedSections;

    @BeforeEach
    public void createOrderedSections() {
        orderedSections = new OrderedSections(
                Arrays.asList(
                        new Section(1L, 4L, 5L, 6),
                        new Section(1L, 1L, 2L, 3),
                        new Section(1L, 3L, 4L, 5),
                        new Section(1L, 2L, 3L, 4)
                )
        );
    }

    @DisplayName("상행부터 하행 순으로 나열된 지하철역 아이디 받아오기.")
    @Test
    public void getOrderedStationIdsTest() {
        assertThat(orderedSections.getOrderedStationIds())
                .containsExactlyElementsOf(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }

    @DisplayName("추가할 지하철 구간 기반으로 나눌 구간 도출하기.")
    @Test
    public void findSectionToSplitTest() {
        assertThat(orderedSections.findSectionToSplit(new Section(1L, 3L, 6L, 5)))
                .isEqualTo(new Section(1L, 3L, 4L, 5));
        assertThat(orderedSections.findSectionToSplit(new Section(1L, 6L, 5L, 5)))
                .isEqualTo(new Section(1L, 4L, 5L, 6));
    }

    @DisplayName("지하철 노선의 끝부분이나 첫부분에 구간 추가하는지 확인하기.")
    @Test
    public void isAddToEdgeCaseTest() {
        assertThat(orderedSections.isAddToEdgeCase(new Section(1L, 8L, 1L, 5)))
                .isTrue();
        assertThat(orderedSections.isAddToEdgeCase(new Section(1L, 5L, 6L, 5)))
                .isTrue();
        assertThat(orderedSections.isAddToEdgeCase(new Section(1L, 4L, 6L, 5)))
                .isFalse();
    }
}
