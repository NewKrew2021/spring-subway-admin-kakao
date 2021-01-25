package subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SectionCreatorTest {

    private SectionCreator sectionCreator;

    @BeforeEach
    void setUp() {
        sectionCreator = new SectionCreator();
    }

    @DisplayName("새로운 구간 생성시, 상/하행역이 모두 기존 구간에 포함되어 있다면 예외가 발생한다")
    @Test
    void createNewSectionFail1() {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sectionCreator.getNextSection(sections, new SectionCreateValue(lineId, 1L, 2L, 2)))
                .withMessage("새로운 구간을 생성할 수 있는 가짓수가 여러개입니다");
    }

    @DisplayName("새로운 구간 생성시, 상/하행역이 모두 기존 구간에 포함되어 있지 않다면 예외가 발생한다")
    @Test
    void createNewSectionFail2() {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sectionCreator.getNextSection(sections, new SectionCreateValue(lineId, 100L, 101L, 2)))
                .withMessage("새로운 구간을 생성할 수 없습니다");
    }

}
