package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void getOrderedStationIdsTest() {
        assertThat(orderedSections.getOrderedStationIds())
                .containsExactlyElementsOf(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    public void findSectionToSplitTest() {
        assertThat(orderedSections.findSectionToSplit(new Section(1L, 3L, 6L, 5)))
                .isEqualTo(new Section(1L, 3L, 4L, 5));
        assertThat(orderedSections.findSectionToSplit(new Section(1L, 6L, 5L, 5)))
                .isEqualTo(new Section(1L, 4L, 5L, 6));
    }

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
