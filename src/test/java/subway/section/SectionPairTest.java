package subway.section;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.section.Section;
import subway.domain.section.SectionPair;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SectionPair 클래스")
public class SectionPairTest {
    Long 신분당선 = 1L;
    Long 강남역 = 1L;
    Long 정자역 = 2L;
    Long 미금역 = 3L;
    Section 강남_정자 = new Section(1L, 신분당선, 강남역, 정자역, 100);
    Section 정자_미금 = new Section(2L, 신분당선, 정자역, 미금역, 100);

    @DisplayName("객체 생성")
    @Test
    public void create() {
        Assertions.assertThatCode(() -> new SectionPair(강남_정자, 정자_미금));
    }

    @DisplayName("merge 기능 테스트")
    @Test
    public void mergeTest() {
        //given
        SectionPair pair = new SectionPair(강남_정자, 정자_미금);
        Section 강남_미금 = new Section(신분당선, 강남역, 미금역, 200);

        //when
        Section mergedSection = pair.merge();

        //then
        assertThat(mergedSection).isEqualTo(강남_미금);
    }
}
