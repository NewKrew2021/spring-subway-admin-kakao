package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        uppermostSection = new Section(1, LINE_ID, 3, -2);
        middleSection = new Section(2, LINE_ID, 5, 2);
        lowermostSection = new Section(3, LINE_ID, 7, 6);
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

    @Test
    void testGetStationIDs() {
        assertThat(sections.getStationIDs()).containsExactly(3L, 5L, 7L);
    }

    @DisplayName("하행종점이 새로운 역")
    @Test
    void insertNewLowermostSection() {
        Section lowermost = new Section(LINE_ID, lowermostSection.getStationID(), 0);
        Section lowerThanLowermost = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 3);

        Section newSection = sections.insert(lowermost, lowerThanLowermost);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 9));
        assertThat(newSection.getDistance()).isEqualTo(9);
    }

    @DisplayName("상행종점이 새로운 역")
    @Test
    void insertNewUppermostSection() {
        Section upperThanUppermost = new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, 0);
        Section uppermost = new Section(LINE_ID, uppermostSection.getStationID(), 3);

        Section newSection = sections.insert(upperThanUppermost, uppermost);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, -5));
        assertThat(newSection.getDistance()).isEqualTo(-5);
    }

    @DisplayName("중간 상행점에 Section을 삽입")
    @Test
    void insertNewInMiddleLeft() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleLeft = new Section(LINE_ID, MIDDLE_LEFT_ID, 3);

        Section newSection = sections.insert(middleLeft, middle);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_LEFT_ID, -1));
        assertThat(newSection.getDistance()).isEqualTo(-1);
    }

    @DisplayName("중간 하행점에 Section을 삽입")
    @Test
    void insertNewInMiddleRight() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleRight = new Section(LINE_ID, MIDDLE_RIGHT_ID, 3);

        Section newSection = sections.insert(middle, middleRight);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_RIGHT_ID, 5));
        assertThat(newSection.getDistance()).isEqualTo(5);
    }

    @DisplayName("지하철 노선에 이미 등록되어있는 역을 등록한다")
    @Test
    void bothSectionAlreadyExists() {
        assertThat(sections.insert(middleSection, lowermostSection)).isNull();
    }

    @DisplayName("지하철 노선에 등록되지 않은 역을 기준으로 등록한다")
    @Test
    void noneOfSectionsExists() {
        Section nonExistingSection1 = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);
        Section nonExistingSection2 = new Section(LINE_ID, MIDDLE_RIGHT_ID, 2);

        assertThat(sections.insert(nonExistingSection1, nonExistingSection2)).isNull();
    }

    @DisplayName("추가하려는 노선이 기존 노선의 거리 이상이다")
    @Test
    void invalidDistanceIsNotInsertable() {
        Section middle = new Section(LINE_ID, middleSection.getStationID(), 0);
        Section middleRight = new Section(LINE_ID, MIDDLE_RIGHT_ID, 4);

        assertThat(sections.insert(middle, middleRight)).isNull();
    }

    @Test
    void testHasMinimumSectionCount() {
        assertThat(sections.hasMinimumSectionCount()).isFalse();
        assertThat(new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 0),
                new Section(LINE_ID, 2, 1)
        )).hasMinimumSectionCount()).isTrue();
    }

    @Test
    void testHasNoSections() {
        assertThat(new Sections(Collections.emptyList()).hasNoSections()).isTrue();
        assertThat(sections.hasNoSections()).isFalse();
    }

    @Test
    void testAreInsertableSections() {
        Section newSection = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);

        assertThat(sections.areValidSections(middleSection, newSection)).isTrue();
    }

    @Test
    void testAreNotInsertableSections() {
        Section nonExistingSection1 = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 0);
        Section nonExistingSection2 = new Section(LINE_ID, MIDDLE_LEFT_ID, 0);

        assertThat(sections.areValidSections(uppermostSection, middleSection)).isFalse();
        assertThat(sections.areValidSections(nonExistingSection1, nonExistingSection2)).isFalse();
    }

    @DisplayName("기존에 존재하는 sections에 새로운 Section이 삽입 가능한지 여부")
    @Test
    void testHaveValidDistance() {
        assertThat(sections.haveValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_RIGHT_ID, 5)
        )).isTrue();

        assertThat(sections.haveValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_LEFT_ID, -1)
        )).isTrue();

        assertThat(sections.haveValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_RIGHT_ID, 6)
        )).isFalse();

        assertThat(sections.haveValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_LEFT_ID, -2)
        )).isFalse();
    }

    @Test
    void middleSectionsHaveNextSection() {
        Section lowerSection = new Section(LINE_ID, MIDDLE_RIGHT_ID, 6);
        Section upperSection = new Section(LINE_ID, MIDDLE_LEFT_ID, 2);

        assertThat(sections.getNextSection(middleSection, lowerSection)).isNotNull();
        assertThat(sections.getNextSection(middleSection, upperSection)).isNotNull();
    }

    @Test
    void terminalSectionsDoesNotHaveNextSection() {
        Section upperThanUppermost = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, -3);
        Section lowerThanLowermost = new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, 7);

        assertThat(sections.getNextSection(uppermostSection, upperThanUppermost)).isNull();
        assertThat(sections.getNextSection(lowermostSection, lowerThanLowermost)).isNull();
    }

    @Test
    void testGetNextSectionIdx() {
        assertThat(sections.getNextSectionIdx(middleSection, uppermostSection)).isEqualTo(0);
        assertThat(sections.getNextSectionIdx(middleSection, lowermostSection)).isEqualTo(2);
    }

    @Test
    void testFindSection() {
        Section nonExistingSection = new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, -2);

        assertThat(sections.findSection(middleSection)).isEqualTo(middleSection);
        assertThat(sections.findSection(null)).isNull();
        assertThat(sections.findSection(nonExistingSection)).isNull();
    }
}
