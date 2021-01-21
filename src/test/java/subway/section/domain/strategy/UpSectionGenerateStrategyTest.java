package subway.section.domain.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.section.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

public class UpSectionGenerateStrategyTest {

    private Section section;

    @BeforeEach
    void create() {
        section = new Section(1L,1L,0);
    }

    @DisplayName("상행점이 생성되는 경우, 하행점의 거리로 부터 주어진 거리만큼 빼진 상대적 위치를 상행점 위치로 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "4,1,-1", "5,2,-2", "5,3,-3",
    })
    void generateTest(Long upStationId, int distance, int expected) {
        //given @params

        //when
        SectionGenerateStrategy sectionGenerateStrategy =
                new UpSectionGenerateStrategy().make(section,upStationId,distance,Integer.MIN_VALUE);

        //then
        assertThat(sectionGenerateStrategy.getNewSection().getRelativePosition()).isEqualTo(expected);
    }

}
