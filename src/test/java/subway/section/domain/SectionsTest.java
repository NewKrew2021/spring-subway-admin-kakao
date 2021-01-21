package subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {
    private final long LINE_ID = 1;
    private final long LOWER_THAN_LOWERMOST_ID = 2;
    private final long MIDDLE_LEFT_ID = 4;
    private final long MIDDLE_RIGHT_ID = 6;
    private final long UPPER_THAN_UPPERMOST_ID = 8;
    private Sections sections;
    private Section uppermostSection;
    private Section middleSection;
    private Section lowermostSection;

    @BeforeEach
    void setUp() {
        uppermostSection = new Section(LINE_ID, 3, -2);
        middleSection = new Section(LINE_ID, 5, 2);
        lowermostSection = new Section(LINE_ID, 7, 6);
        sections = new Sections(Arrays.asList(
                uppermostSection,
                middleSection,
                lowermostSection
        ));
    }

    @DisplayName("중복된 Section을 갖고 있거나 정렬되어 있지 않은 경우")
    @Test
    void invalidSections() {
        assertThatThrownBy(() -> new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 0),
                new Section(LINE_ID, 1, 0)
        ))).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 3),
                new Section(LINE_ID, 2, 0)
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간에 포함되는 역의 ID를 모두 조회한다")
    @Test
    void testGetStationIDs() {
        assertThat(sections.getStationIDs()).containsExactly(3L, 5L, 7L);
    }

    @DisplayName("하행종점이 새로운 역")
    @Test
    void insertNewLowermostSection() {
        Section lowermost = new Section(LINE_ID, lowermostSection.getStationID(), 0);
        Section lowerThanLowermost = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 3);

        Section newSection = sections.createSection(lowermost, lowerThanLowermost);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 9));
        assertThat(newSection.getDistance()).isEqualTo(9);
    }

    @DisplayName("상행종점이 새로운 역")
    @Test
    void insertNewUppermostSection() {
        Section upperThanUppermost = new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, 0);
        Section uppermost = new Section(LINE_ID, uppermostSection.getStationID(), 3);

        Section newSection = sections.createSection(upperThanUppermost, uppermost);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, -5));
        assertThat(newSection.getDistance()).isEqualTo(-5);
    }

    @DisplayName("중간 상행점에 Section을 삽입")
    @Test
    void insertNewInMiddleLeft() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleLeft = new Section(LINE_ID, MIDDLE_LEFT_ID, 3);

        Section newSection = sections.createSection(middleLeft, middle);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_LEFT_ID, -1));
        assertThat(newSection.getDistance()).isEqualTo(-1);
    }

    @DisplayName("중간 하행점에 Section을 삽입")
    @Test
    void insertNewInMiddleRight() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleRight = new Section(LINE_ID, MIDDLE_RIGHT_ID, 3);

        Section newSection = sections.createSection(middle, middleRight);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_RIGHT_ID, 5));
        assertThat(newSection.getDistance()).isEqualTo(5);
    }

    @DisplayName("지하철 노선에 이미 등록되어있는 역을 등록한다")
    @Test
    void bothSectionAlreadyExists() {
        assertThatThrownBy(() -> sections.createSection(middleSection, lowermostSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already");
    }

    @DisplayName("지하철 노선에 등록되지 않은 역을 기준으로 등록한다")
    @Test
    void noneOfSectionsExists() {
        Section nonExistingSection1 = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);
        Section nonExistingSection2 = new Section(LINE_ID, MIDDLE_RIGHT_ID, 2);

        assertThatThrownBy(() -> sections.createSection(nonExistingSection1, nonExistingSection2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("neither");
    }

    @DisplayName("추가하려는 노선이 기존 노선의 거리 이상이다")
    @Test
    void invalidDistanceIsNotInsertable() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleRight = new Section(LINE_ID, MIDDLE_RIGHT_ID, 4);

        assertThat(sections.createSection(middle, middleRight)).isNull();
    }

    @DisplayName("상행/하행역으로 이루어진 최소 단위의 구간이다")
    @Test
    void testHasMinimumSectionCount() {
        assertThat(sections.hasMinimumSectionCount()).isFalse();
        assertThat(new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 0),
                new Section(LINE_ID, 2, 1)
        )).hasMinimumSectionCount()).isTrue();
    }

    @DisplayName("구간의 초기상태다 (역이 존재하지 않음)")
    @Test
    void testHasNoSections() {
        assertThat(new Sections(Collections.emptyList()).hasNoSections()).isTrue();
        assertThat(sections.hasNoSections()).isFalse();
    }

    @DisplayName("구간에 새로운 역을 추가할 수 있다")
    @Test
    void testAreInsertableSections() {
        Section newSection = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);

        assertThatCode(() -> sections.checkValidSections(middleSection, newSection)).doesNotThrowAnyException();
    }

    @DisplayName("구간에 추가할 역들이 모두 존재하거나 모두 존재하지 않을땐 삽입 불가능하다")
    @Test
    void testAreNotInsertableSections() {
        Section nonExistingSection1 = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 0);
        Section nonExistingSection2 = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);

        assertThatThrownBy(() -> sections.checkValidSections(uppermostSection, middleSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already");
        assertThatThrownBy(() -> sections.checkValidSections(nonExistingSection1, nonExistingSection2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("neither");
    }

    @DisplayName("구간에 있는 역들의 거리와 비교하여 추가가 가능한지 확인한다")
    @ParameterizedTest
    @CsvSource({"5,true", "-1,true", "6,false", "-2,false"})
    void testHaveValidDistance(int distance, boolean expected) {
        assertThat(sections.haveValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_RIGHT_ID, distance)
        )).isEqualTo(expected);
    }

    @DisplayName("중간 역들은 상하에 다른 역이 존재한다")
    @Test
    void middleSectionsHaveNextSection() {
        Section lowerSection = new Section(LINE_ID, MIDDLE_RIGHT_ID, 6);
        Section upperSection = new Section(LINE_ID, MIDDLE_LEFT_ID, 2);

        assertThat(sections.getNextSection(middleSection, lowerSection)).isNotNull();
        assertThat(sections.getNextSection(middleSection, upperSection)).isNotNull();
    }

    @DisplayName("상행/하행 종점은 자신보다 빠른/늦은 역이 없다")
    @Test
    void terminalSectionsDoesNotHaveNextSection() {
        Section upperThanUppermost = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, -3);
        Section lowerThanLowermost = new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, 7);

        assertThat(sections.getNextSection(uppermostSection, upperThanUppermost)).isNull();
        assertThat(sections.getNextSection(lowermostSection, lowerThanLowermost)).isNull();
    }

    @DisplayName("다음 역 조회를 위한 Index를 조회한다")
    @Test
    void testGetNextSectionIdx() {
        assertThat(sections.getNextSectionIdx(middleSection, uppermostSection)).isEqualTo(0);
        assertThat(sections.getNextSectionIdx(middleSection, lowermostSection)).isEqualTo(2);
    }

    @DisplayName("구간 목록에 특정 역을 찾는다")
    @Test
    void testFindSection() {
        Section nonExistingSection = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, -2);

        assertThat(sections.findSection(middleSection)).isEqualTo(middleSection);
        assertThat(sections.findSection(null)).isNull();
        assertThat(sections.findSection(nonExistingSection)).isNull();
    }
}
