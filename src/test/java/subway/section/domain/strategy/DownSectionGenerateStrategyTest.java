package subway.section.domain.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.section.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

public class DownSectionGenerateStrategyTest {

    private Section section;

    @BeforeEach
    void create() {
        section = new Section(1L,1L,0);
    }

    @DisplayName("하행점이 생성되는 경우, 상행점의 거리로 부터 주어진 거리만큼 더해진 상대적 위치를 하행점 위치로 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "2,5,5", "3,19,19", "4,1,1",
    })
    void generateTest(Long downStationId, int distance, int expected) {
        //given @params

        //when
        SectionGenerateStrategy sectionGenerateStrategy =
                new DownSectionGenerateStrategy().make(section,downStationId,distance,Integer.MAX_VALUE);

        //then
        assertThat(sectionGenerateStrategy.getNewSection().getRelativePosition()).isEqualTo(expected);
    }

}
