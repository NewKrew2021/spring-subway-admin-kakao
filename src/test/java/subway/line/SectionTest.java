package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static subway.line.Section.VIRTUAL_ENDPOINT_ID;

class SectionTest {

    @ParameterizedTest
    @DisplayName("0보다 작거나 작은 길이로 구간을 생성할 때 예외가 발생한다.")
    @CsvSource({"-1", "0"})
    void checkInvalidSectionDistance(int distance) {
        assertThatThrownBy(() -> {
            new Section(1L, 1L, 1L, distance);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 양끝점을 포함하면 거리가 무한대이다.")
    void checkEndPointSectionDistance() {
        Section upSection = new Section(1L, VIRTUAL_ENDPOINT_ID, 1L, 2);
        Section downSection = new Section(1L, 1L, VIRTUAL_ENDPOINT_ID, 10);

        assertThat(upSection.getDistance()).isEqualTo(Integer.MAX_VALUE);
        assertThat(downSection.getDistance()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("다른 구간이 주어질 때 상행역이 동일한지 확인한다.")
    void shareUpStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(section.shareUpStation(new Section(1L, 2L, 5L, 7))).isTrue();
        assertThat(section.shareUpStation(new Section(1L, 3L, 5L, 7))).isFalse();
    }

    @Test
    @DisplayName("역 번호가 주어질 때 상행역이 동일한지 확인한다.")
    void isUpStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(section.isUpStation(2L)).isTrue();
        assertThat(section.isUpStation(3L)).isFalse();
    }

    @Test
    @DisplayName("역 번호가 주어질 때 하행역이 동일한지 확인한다.")
    void isDownStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(section.isDownStation(2L)).isFalse();
        assertThat(section.isDownStation(3L)).isTrue();
    }
}