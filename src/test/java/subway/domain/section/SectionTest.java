package subway.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    @DisplayName("두 섹선 사이의 거리를 계산한다.")
    public void calculateDistance() {
        Section upSection = new Section(1L,1L,-32,2L);
        Section downSection = new Section(2L,2L,32 , 2L );
        assertThat(downSection.calculateDistance(upSection)).isEqualTo(64);
        assertThat(upSection.calculateDistance(downSection)).isEqualTo(64);
    }
}
