package subway.section;

import org.junit.jupiter.api.Test;
import subway.exception.NotExistSectionDeleteException;
import subway.exception.TooFewSectionAsDeleteException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SectionTest {

    @Test
    void updateNextStationToOtherNextStationTest() {
        Section section1 = new Section( 1L, 1L, 1L, 1, 2L );
        Section section2 = new Section( 2L, 1L, 2L, 1, 3L );
        Section section3 = new Section( 3L, 1L, 3L, 1, -1L );

        section1.updateNextStationToOtherNextStation(section2);
        section2.updateNextStationToOtherNextStation(section3);

        assertThat(section1.getNextStationId()).isEqualTo(3);
        assertThat(section2.getNextStationId()).isEqualTo(-1);
    }

    @Test
    void updateNextStationToOtherStationTest() {
        Section section1 = new Section( 1L, 1L, 1L, 1, 2L );
        Section section2 = new Section( 2L, 1L, 2L, 1, 3L );
        Section section3 = new Section( 3L, 1L, 3L, 1, -1L );

        section1.updateNextStationToOtherStation(section2);
        section2.updateNextStationToOtherStation(section3);

        assertThat(section1.getNextStationId()).isEqualTo(2);
        assertThat(section2.getNextStationId()).isEqualTo(3);
    }

    @Test
    void isPossibleDeleteTest() {
        Section section1 = new Section( 1L, 1L, 1L, 1, 2L );
        Section nonSection = Section.DO_NOT_EXIST_SECTION;

        assertThat(section1.isPossibleDelete(3)).isTrue();
        assertThatThrownBy(() -> section1.isPossibleDelete(2)).isInstanceOfAny(TooFewSectionAsDeleteException.class);
        assertThatThrownBy(() -> nonSection.isPossibleDelete(3)).isInstanceOf(NotExistSectionDeleteException.class);
    }

}
