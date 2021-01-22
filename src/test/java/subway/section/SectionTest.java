package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exceptions.IllegalSectionCreateException;
import subway.exceptions.IllegalSectionSubtraction;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Section 클래스")
public class SectionTest {

    @DisplayName("잘못된 distance로 section 생성")
    @Test
    public void createAbnormally_1() {
        assertThatThrownBy(() -> {
            new Section(1L, 2L, 3L, 4L, 0);
        }).isInstanceOf(IllegalSectionCreateException.class);
    }

    @DisplayName("upstation과 downstation이 동일한 section을 생성")
    @Test
    public void createAbnormally_2() {
        assertThatThrownBy(() -> {
            new Section(1L, 2L, 3L, 3L, 100);
        }).isInstanceOf(IllegalSectionCreateException.class);
    }

    @DisplayName("upstation기준 subtract: 정상적인 section subtraction")
    @Test
    public void normallySubtractBasedOnUpStationTest() {
        //given
        Section targetSection = new Section(1L, 2L, 3L, 5L, 100);
        Section subtractingSection = new Section(2L, 2L, 3L, 4L, 50);
        Section subtractResultSection = new Section(1L, 2L, 4L, 5L, 50);

        //when
        Section remainSection = targetSection.subtractBasedOnUpStation(subtractingSection);

        //then
        assertThat(remainSection).isEqualTo(subtractResultSection);
    }

    @DisplayName("upstation기준 subtract: upstation은 다르고, 대신 downStation이 같은 경우")
    @Test
    public void AbnormallySubtractBasedOnUpStationTest_1() {
        //given
        Section targetSection = new Section(1L, 2L, 3L, 5L, 100);
        Section subtractingSection = new Section(2L, 2L, 4L, 5L, 50);

        //when, then
        assertThatThrownBy(() -> {
            Section remainSection = targetSection.subtractBasedOnUpStation(subtractingSection);
        }).isInstanceOf(IllegalSectionSubtraction.class);
    }

    @DisplayName("downStation기준 subtract: 정상적인 section subtraction")
    @Test
    public void normallySubtractBasedOnDownStationTest() {
        //given
        Section targetSection = new Section(1L, 2L, 3L, 5L, 100);
        Section subtractingSection = new Section(2L, 2L, 4L, 5L, 50);
        Section subtractResultSection = new Section(1L, 2L, 3L, 4L, 50);

        //when
        Section remainSection = targetSection.subtractBasedOnDownStation(subtractingSection);

        //then
        assertThat(remainSection).isEqualTo(subtractResultSection);
    }

    @DisplayName("downStation기준 subtract: downStation은 다르고, 대신 upStation이 같은 경우")
    @Test
    public void AbnormallySubtractBasedOnDownStationTest_1() {
        //given
        Section targetSection = new Section(1L, 2L, 3L, 5L, 100);
        Section subtractingSection = new Section(2L, 2L, 3L, 4L, 50);

        //when, then
        assertThatThrownBy(() -> {
            Section remainSection = targetSection.subtractBasedOnDownStation(subtractingSection);
        }).isInstanceOf(IllegalSectionSubtraction.class);
    }
}
