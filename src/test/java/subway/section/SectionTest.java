package subway.section;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.section.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @DisplayName("상대적인 위치 2개가 주어졌을 때, 그 차이를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,-3", "1,5,-4", "10,9,1", "2,6,-4", "0,0,0"
    })
    void compareDistanceTest(int RelativePositionStandard, int RelativePositionCompare, int expected) {
        //given
        Section sectionStandard = new Section(1L,1L,RelativePositionStandard);
        Section sectionCompare = new Section(1L,2L,RelativePositionCompare);

        //when,then
        assertThat(sectionStandard.comparePosition(sectionCompare)).isEqualTo(expected);

    }

}
