package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.InvalidSectionException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Sections sections;

    @BeforeEach
    public void setup() {
        sections = new Sections(Arrays.asList(
                new Section(2L,0,2L),
                new Section(3L,5,2L)));
    }

    @Test
    @DisplayName("노선의 마지막 구간을 삭제하려고 시도합니다")
    public void When_ThereisOnly2Stations_then_deletableThrowException() {
        assertThatThrownBy(() ->
                sections.deletable(3L)
        ).isInstanceOf(InvalidSectionException.class).hasMessage(Sections.LAST_SECTION_DELETE_ERROR);
    }

    @Test
    @DisplayName("노선에 존재하지 않는 역을 삭제하려고 합니다.")
    public void When_ThereisNoSuchStation_then_deletableThrowException() {
        sections = new Sections(Arrays.asList(
                new Section(2L,0,2L),
                new Section(3L,5,2L),
                new Section(4L,10,2L)));

        assertThatThrownBy(() ->
                sections.deletable(5L)
        ).isInstanceOf(InvalidSectionException.class).hasMessage(Sections.NO_SUCH_STATION_DELETE_ERROR);
    }

    @Test
    @DisplayName("추가하려는 구간의 하행역이 상행 종점이고, 상행 종점에 역을 추가합니다.")
    public void testAddSection1() {
        assertThat(sections.addSection(4L,2L,10)).isEqualTo(new Section(4L,-10,2L));
    }

    @Test
    @DisplayName("추가하려는 구간의 상행역이 하행 종점이고, 하행 종점에 역을 추가합니다.")
    public void testAddSection2() {
        assertThat(sections.addSection(3L,4L,10)).isEqualTo(new Section(4L,15,2L));
    }

    @Test
    @DisplayName("추가하려는 구간의 상행역만 존재하고, 존재하는 구간 중간에 역을 추가합니다.")
    public void testAddSection3() {
        assertThat(sections.addSection(2L,4L,3)).isEqualTo(new Section(4L,3,2L));
    }

    @Test
    @DisplayName("추가하려는 구간의 하행역만 존재하고, 존재하는 구간 중간에 역을 추가합니다.")
    public void testAddSection4() {
        assertThat(sections.addSection(4L,3L,3)).isEqualTo(new Section(4L,2,2L));
    }

    @Test
    @DisplayName("추가하려는 구간의 두 지점 모두 구간에 존재하지 않습니다.")
    public void testInvalidAddSection1() {
        assertThatThrownBy(() -> sections.addSection(4L,5L,3))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessage(Sections.NO_CONNECTED_SECTION_ADD_ERROR);
    }

    @Test
    @DisplayName("이미 존재하는 구간을 다시 추가합니다.")
    public void testInvalidAddSection2() {
        assertThatThrownBy(() -> sections.addSection(2L,3L,15))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessage(Sections.ALREADY_EXIST_SECTION_ADD_ERROR);
    }

    @Test
    @DisplayName("이미 존재하는 구간의 길이보다 추가하려는 구간의 길이가 더 깁니다.")
    public void testInvalidAddSection3() {
        assertThatThrownBy(() -> sections.addSection(2L,4L,15))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessage(Sections.INVALID_DISTANCE_SECTION_ADD_ERROR);
    }



}
