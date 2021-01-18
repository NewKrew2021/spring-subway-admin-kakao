package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @DisplayName("출발역과 도착역이 같은 구간은 생성되지 않는다")
    @Test
    void createFail1() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Section(1L, 3L, 3L, 3))
                .withMessage("출발역과 도착역은 같을 수 없습니다.");
    }

    @DisplayName("구간의 길이가 0 이하면 구간은 생성되지 않는다")
    @Test
    void createFail2() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Section(1L, 2L, 3L, 0))
                .withMessage("거리는 0보다 커야 합니다.");
    }

    /**
     * 구간1: 2- - - - -3
     * 구간2: 2- - -4
     * 결과 :       4- -3
     */
    @DisplayName("상행역이 같은 두 구간 중 한 구간에서 다른 구간을 뺀 새로운 구간을 구한다")
    @Test
    void subtract1() {
        // given
        Section section = new Section(1L, 2L, 3L, 5);
        Section other = new Section(1L, 2L, 4L, 3);

        // when
        Section result = section.subtractWith(other);

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 4L, 3L, 2));

    }

    /**
     * 구간1: 3- - - - -4
     * 구간2:     2- - -4
     * 결과 : 3- -2
     */
    @DisplayName("하행역이 같은 두 구간 중 한 구간에서 다른 구간을 뺀 새로운 구간을 구한다")
    @Test
    void subtract2() {
        // given
        Section section = new Section(1L, 3L, 4L, 5);
        Section other = new Section(1L, 2L, 4L, 3);

        // when
        Section result = section.subtractWith(other);

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 3L, 2L, 2));

    }

    /**
     * 구간1:     3- - -4
     * 구간2: 2- - - - -4
     * 결과 : 예외
     */
    @DisplayName("빼려는 구간의 거리가 기존 구간의 거리 이상이면 예외가 발생한다")
    @Test
    void subtractFail1() {
        // given
        Section section = new Section(1L, 3L, 4L, 3);
        Section other = new Section(1L, 2L, 4L, 5);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.subtractWith(other))
                .withMessage("구간의 거리는 현재 구간의 거리보다 더 짧아야 합니다.");
    }

    /**
     * 구간1: 2- - - - - -4
     * 구간2: 2- - - - -4
     * 결과 : 예외
     */
    @DisplayName("기존 구간과 빼려는 구간의 상/하행역이 모두 일치하면 예외가 발생한다")
    @Test
    void subtractFail2() {
        // given
        Section section = new Section(1L, 2L, 4L, 6);
        Section other = new Section(1L, 2L, 4L, 5);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.subtractWith(other))
                .withMessage("상/하행역 중 단 하나만 일치해야 합니다.");
    }

    /**
     * 구간1: 3- - -5
     * 구간2: 2- -4
     * 결과 : 예외
     */
    @DisplayName("기존 구간과 빼려는 구간의 상행역과 하행역이 모두 일치하지 않으면 예외가 발생한다")
    @Test
    void subtractFail3() {
        // given
        Section section = new Section(1L, 3L, 5L, 3);
        Section other = new Section(1L, 2L, 4L, 2);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.subtractWith(other))
                .withMessage("상/하행역 중 단 하나만 일치해야 합니다.");
    }

    @DisplayName("기존 구간과 빼려는 구간의 노선이 일치하지 않으면 예외가 발생한다")
    @Test
    void subtractFail4() {
        // given
        Section section = new Section(1L, 2L, 5L, 6);
        Section other = new Section(2L, 2L, 4L, 5);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.subtractWith(other))
                .withMessage("같은 노선이어야 합니다.");
    }

    /**
     * 구간1: 2- -3
     * 구간2:     3- - -4
     * 결과 : 2- - - - -4
     */
    @DisplayName("한 구간의 하행역이 다른 구간의 상행역과 일치하는(연속되는) 구간을 합친 구간을 구한다")
    @Test
    void merge() {
        // given
        Section section1 = new Section(1L, 2L, 3L, 2);
        Section section2 = new Section(1L, 3L, 4L, 3);

        // when
        Section result1 = section1.mergeWith(section2);
        Section result2 = section2.mergeWith(section1);

        // then
        assertAll(
                () -> assertThat(result1).usingRecursiveComparison()
                        .isEqualTo(new Section(1L, 2L, 4L, 5)),
                () -> assertThat(result1).usingRecursiveComparison()
                        .isEqualTo(result2)
        );
    }

    @DisplayName("기존 구간과 합치려는 구간의 노선이 일치하지 않으면 예외가 발생한다")
    @Test
    void mergeFail1() {
        // given
        Section section = new Section(1L, 2L, 5L, 6);
        Section other = new Section(2L, 5L, 4L, 5);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.mergeWith(other))
                .withMessage("같은 노선이어야 합니다.");
    }

    /**
     * 구간1: 1- -2
     * 구간2:       3- -4
     * 결과 : 예외
     */
    @DisplayName("두 구간을 합칠 때 연속하지 않은 구간이면 예외가 발생한다")
    @Test
    void mergeFail2() {
        // given
        Section section = new Section(1L, 1L, 2L, 2);
        Section other = new Section(1L, 3L, 4L, 2);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> section.mergeWith(other))
                .withMessage("연속된 구간이어야 합니다.");
    }

    @DisplayName("두 구간의 상행역이 같은지 확인한다")
    @ParameterizedTest
    @CsvSource({"1,1,true", "1,2,false"})
    void sameUpStation(long upStation1, long upStation2, boolean expected) {
        // given
        Section section = new Section(1L, upStation1, 4L, 3);
        Section other = new Section(1L, upStation2, 5L, 4);

        // when
        boolean result = section.hasSameUpStation(other);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("두 구간의 하행역이 같은지 확인한다")
    @ParameterizedTest
    @CsvSource({"1,1,true", "1,2,false"})
    void sameDownStation(long downStation1, long downStation2, boolean expected) {
        // given
        Section section = new Section(1L, 4L, downStation1, 3);
        Section other = new Section(1L, 5L, downStation2, 4);

        // when
        boolean result = section.hasSameDownStation(other);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("구간이 상행 종점 구간인지 확인한다")
    @ParameterizedTest
    @CsvSource({"-1,true", "1,false"})
    void upTerminal(long upTerminalId, boolean expected) {
        // given
        Section section = new Section(1L, upTerminalId, 4L, 3);

        // when
        boolean result = section.isUpTerminal();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("구간이 하행 종점 구간인지 확인한다")
    @ParameterizedTest
    @CsvSource({"-1,true", "1,false"})
    void downTerminal(long downTerminalId, boolean expected) {
        // given
        Section section = new Section(1L, 4L, downTerminalId, 3);

        // when
        boolean result = section.isDownTerminal();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("구간의 상/하행역 중 주어진 역이 포함되는지 확인한다")
    @Test
    void containsStation() {
        Section section2To3 = new Section(1L, 2L, 3L, 4);
        Section section3To4 = new Section(1L, 3L, 4L, 4);
        Section section4To5 = new Section(1L, 4L, 5L, 4);
        long station3 = 3L;

        assertAll(
                () -> assertThat(section2To3.containsStation(station3)).isTrue(),
                () -> assertThat(section3To4.containsStation(station3)).isTrue(),
                () -> assertThat(section4To5.containsStation(station3)).isFalse()
        );
    }
}
