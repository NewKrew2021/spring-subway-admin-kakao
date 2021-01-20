package subway.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.station.Station;
import subway.exception.section.SectionAttachException;
import subway.exception.section.SectionDistanceException;
import subway.exception.section.SectionSplitException;
import subway.exception.section.StationDuplicationException;

import static org.assertj.core.api.Assertions.*;

public class SectionTest {
    private final Long LINE_ID = 1L;
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");
    private final Station 망포역 = new Station(4L, "망포역");

    private final Section 강남_망포 = new Section(1L, LINE_ID, 강남역, 망포역, 10);
    private final Section 강남_역삼 = new Section(2L, LINE_ID, 강남역, 역삼역, 2);
    private final Section 광교_망포 = new Section(3L, LINE_ID, 광교역, 망포역, 2);
    private final Section 역삼_광교 = new Section(4L, LINE_ID, 역삼역, 광교역, 5);

    @Test
    @DisplayName("Section 생성 조건 테스트")
    public void constructorTest() {
        assertThatThrownBy(() -> new Section(1L, LINE_ID, 강남역, 강남역, 10))
                .isInstanceOf(StationDuplicationException.class);

        assertThatThrownBy(() -> new Section(1L, LINE_ID, 강남역, 역삼역, 0))
                .isInstanceOf(SectionDistanceException.class);
    }

    @Test
    @DisplayName("Section 분할 테스트")
    public void splitSectionTest() {
        assertThat(강남_망포.split(강남_역삼)).isEqualTo(new Section(null, LINE_ID, 역삼역, 망포역, 8));
        assertThat(강남_망포.split(광교_망포)).isEqualTo(new Section(null, LINE_ID, 강남역, 광교역, 8));

        assertThatThrownBy(() -> 강남_망포.split(역삼_광교)).isInstanceOf(SectionSplitException.class);
    }

    @Test
    @DisplayName("Section 병합 테스트")
    public void attachSectionTest() {
        assertThat(강남_역삼.attach(역삼_광교)).isEqualTo(new Section(null, LINE_ID, 강남역, 광교역, 7));
        assertThatThrownBy(() -> 강남_역삼.attach(강남_망포)).isInstanceOf(SectionAttachException.class);
    }
}
