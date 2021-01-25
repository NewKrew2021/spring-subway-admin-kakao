package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class SectionsTest {

    private static Station 강남역;
    private static Station 양재역;
    private static Station 판교역;
    private static Station 양재시민의숲역;
    private static Station 정자역;
    private static Line 신분당선;
    private Section 구간1;
    private Section 구간2;
    private Section 구간3;
    private Sections sections;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
        판교역 = new Station(3L, "판교역");
        양재시민의숲역 = new Station(4L, "양재시민의숲역");
        정자역 = new Station(5L, "정자역");

        신분당선 = new Line(1L, "신분당선", "bg-red-600");

        구간1 = Section.of(신분당선, 강남역, 양재역, 10);
        구간2 = Section.of(신분당선, 양재역, 양재시민의숲역, 10);
        구간3 = Section.of(신분당선, 양재시민의숲역, 판교역, 10);

        sections = new Sections(Arrays.asList(구간1, 구간2, 구간3));
    }

    /**        상행종점                     하행종점
     * 구간리스트 강남역 - 양재역 - 양재시민의숲역 - 판교역
     */
    @DisplayName("상행 종점이 강남역이 나와야 한다.")
    @Test
    void testIsHeadStation() {
        //when
        Section head = sections.findHeadSection();
        //then
        assertThat(head.getUpStation()).isEqualTo(강남역);

    }

    @DisplayName("하행 종점이 판교역이 나와야 한다.")
    @Test
    void testIsTailStation() {
        //when
        Section tail = sections.findTailSection();
        //then
        assertThat(tail.getDownStation()).isEqualTo(판교역);
    }

    @DisplayName("구간의 upStation downStation 순서가 바뀌어도 같은 구간으로 판단한다.")
    @Test
    void testHasSameSection() {
        //when
        Section section1 = Section.of(신분당선, 양재역, 강남역, 10);
        Section section2 = Section.of(신분당선, 판교역, 정자역, 20);

        //then
        assertThat(sections.hasSameSection(section1)).isEqualTo(true);
        assertThat(sections.hasSameSection(section2)).isEqualTo(false);
    }

    @DisplayName("stationId이 주어지면 바로 이전 구간을 반환하고, 만약 상행종점인 경우 IllegalArgumentException 던진다.")
    @Test
    void testFindFrontSection() {
        //given
        Long 강남역_id = 강남역.getId();
        Long 양재역_id = 양재역.getId();
        Long 판교역_id = 판교역.getId();

        //when

        //then
        assertThat(sections.findFrontSection(양재역_id)).isEqualTo(구간1);
        assertThat(sections.findFrontSection(판교역_id)).isEqualTo(구간3);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.findFrontSection(강남역_id));
    }

    @DisplayName("stationId이 주어지면 바로 다음 구간 반환하고, 만약 하행종점인 경우 IllegalArgumentException 던진다.")
    @Test
    void testFindRearSection() {
        //given
        Long 강남역_id = 강남역.getId();
        Long 판교역_id = 판교역.getId();

        //when

        //then
        assertThat(sections.findRearSection(강남역_id)).isEqualTo(구간1);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.findRearSection(판교역_id));
    }

    @DisplayName("종점이 포함된 구간을 반환한다.")
    @Test
    void testTerminalSection(){
        //when
        Section head = sections.findHeadSection();
        Section tail = sections.findTailSection();

        //then
        assertThat(head).isEqualTo(구간1);
        assertThat(tail).isEqualTo(구간3);
    }

    @DisplayName("추가할 구간이 주어졌을 때 종점이 바뀌는 경우 true, 아닌경우 false를 반환한다.")
    @Test
    void testIsExtendTerminal(){
        //given
        Station 신상행역 = new Station(7L, "신상행역");
        Station 신하행역 = new Station(8L, "신하행역");
        Station 갈래역 = new Station(9L,"갈래역");
        Section section1 = Section.of(신분당선, 신상행역, 강남역,2);
        Section section2 = Section.of(신분당선, 강남역, 갈래역, 2);
        Section section3 = Section.of(신분당선, 판교역, 신하행역, 2);

        //when
        //then
        assertThat(sections.isExtendTerminal(section1)).isEqualTo(true);
        assertThat(sections.isExtendTerminal(section2)).isEqualTo(false);
        assertThat(sections.isExtendTerminal(section3)).isEqualTo(true);
    }


}