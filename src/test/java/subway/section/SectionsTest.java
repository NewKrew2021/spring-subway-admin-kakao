package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {
    private final Long LINE_ID = 1L;
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");
    private final Station 망포역 = new Station(4L, "망포역");

    private final Section 강남_역삼 = new Section(1L, LINE_ID, 강남역, 역삼역, 5);
    private final Section 역삼_광교 = new Section(2L, LINE_ID, 역삼역, 광교역, 5);
    private final Section 광교_망포 = new Section(3L, LINE_ID, 광교역, 망포역, 5);


    @Test
    @DisplayName("섹션 생성 시 정렬 테스트")
    public void orderSectionTest() {
        List<Section> sectionList = Arrays.asList(강남_역삼, 역삼_광교);
        assertThat(new Sections(sectionList).getAllStations()).containsExactly(강남역, 역삼역, 광교역);

        sectionList = Arrays.asList(역삼_광교, 강남_역삼);
        assertThat(new Sections(sectionList).getAllStations()).containsExactly(강남역, 역삼역, 광교역);
    }

    @Test
    @DisplayName("노선 시작 지점 테스트")
    public void findFirstStationTest() {
        List<Section> sectionList = Arrays.asList(강남_역삼, 역삼_광교);
        assertThat(new Sections(sectionList).findFirstStation()).isEqualTo(강남역.getId());

        sectionList = Arrays.asList(역삼_광교, 강남_역삼);
        assertThat(new Sections(sectionList).findFirstStation()).isEqualTo(강남역.getId());
    }
}
