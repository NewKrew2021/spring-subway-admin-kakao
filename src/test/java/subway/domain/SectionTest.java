package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.custom.IllegalDistanceException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철 구간 테스트")
public class SectionTest {
    private Section section1;
    private Section section2;
    private Section mergedSection;

    @BeforeEach
    public void setUp(){
        section1 = new Section(1L, 1L, 2L, 3);
        section2 = new Section(1L, 2L, 3L, 4);
        mergedSection = new Section(1L, 1L, 3L, 7);
    }

    @DisplayName("구간 분리 시, 주어진 구간의 나머지 구간 도출 테스트")
    @Test
    public void getAnotherSectionTest(){
        assertThat(mergedSection.getAnotherSection(section1)).isEqualTo(section2);
        assertThat(mergedSection.getAnotherSection(section2)).isEqualTo(section1);
        assertThatThrownBy(() -> section1.getAnotherSection(section2)).isInstanceOf(IllegalDistanceException.class);
        assertThatThrownBy(() -> section2.getAnotherSection(section1)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 합 시, 합체된 구간 도출 테스트")
    @Test
    public void mergeSectionTest(){
        assertThat(section1.mergeSection(section2)).isEqualTo(mergedSection);
        assertThat(section2.mergeSection(section1)).isEqualTo(mergedSection);
        assertThatThrownBy(() -> mergedSection.mergeSection(section1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> mergedSection.mergeSection(section2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section1.mergeSection(section1)).isInstanceOf(IllegalArgumentException.class);
    }
}
