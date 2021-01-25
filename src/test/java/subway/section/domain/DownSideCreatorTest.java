package subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DownSideCreatorTest {

    private DownSideCreator downSideCreator = new DownSideCreator();

    @BeforeEach
    void setUp() {
        downSideCreator = new DownSideCreator();
    }

    /**
     * 상행  - - - - - - 하행
     * 케이스1(o):     1 -> new    2
     * 케이스2(x):     new <- 1    2
     * 케이스3(x):     1    new <- 2
     * 케이스4(o):     1           2 -> new
     * <p>
     * 10은 새로 추가될 구간의 역 id
     */
    @DisplayName("추가할 구간이 주어지면 Sections에 하행 방향으로 추가할 수 있는지 검사한다")
    @ParameterizedTest
    @CsvSource({"1,10,true", "10,1,false", "10,2,false", "2,10,true"})
    void createNewSection(long upStationId, long downStationId, boolean expected) {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));
        SectionCreateValue sectionCreateValue = new SectionCreateValue(lineId, upStationId, downStationId, 3);

        // when
        boolean result = downSideCreator.isSupport(sections, sectionCreateValue);

        // then
        assertThat(result).isEqualTo(expected);
    }

    /**
     * 10은 새로 추가될 구간의 역 id
     * <p>
     * 위치:       -2 -1 0 1 2 3 4 5 6 7 8 9
     * 케이스_하나:        1 ->  x   2
     * 케이스_두울:        1         2 ->  x
     */
    @DisplayName("하행 방향 추가할 구간이 주어지면 Sections에 하행 방향으로 추가한 구간을 구한다")
    @ParameterizedTest
    @CsvSource({"1,10,3,3", "2,10,3,8"})
    void createNewSection(long upStationId, long downStationId, int distance, int expectedPosition) {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));
        SectionCreateValue sectionCreateValue = new SectionCreateValue(lineId, upStationId, downStationId, distance);

        // when
        Section result = downSideCreator.create(sections, sectionCreateValue);

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new Section(lineId, 10, expectedPosition));
    }
}
