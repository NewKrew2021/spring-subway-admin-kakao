package subway.section;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SectionsTest {

    @Test
    void getSortingStationIdTest() {
        Section section1 = new Section( 1L, 1L, 1L, 1, 2L );
        Section section2 = new Section( 2L, 1L, 2L, 1, 3L );
        Section section3 = new Section( 3L, 1L, 3L, 1, 4L );
        Section section4 = new Section( 4L, 1L, 4L, 1, 5L );
        Section section5 = new Section( 5L, 1L, 5L, 1, -1L );

        Sections sections = new Sections(Arrays.asList(new Section[]{section2, section4, section1, section5, section3}));

        List<Long> stations = sections.getSortingStationId();

        assertThat(stations.get(0)).isEqualTo(1);
        assertThat(stations.get(1)).isEqualTo(2);
        assertThat(stations.get(2)).isEqualTo(3);
        assertThat(stations.get(3)).isEqualTo(4);
        assertThat(stations.get(4)).isEqualTo(5);
    }
}
