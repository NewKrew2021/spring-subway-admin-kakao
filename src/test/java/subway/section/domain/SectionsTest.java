package subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.section.dto.SectionRequest;
import subway.section.vo.SectionCreateValue;

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

    @DisplayName("중복된 Section을 갖고 있을 경")
    @Test
    void hasDuplicates() {
        assertThatThrownBy(() -> new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 0),
                new Section(LINE_ID, 1, 0)
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("위치 기준으로 정렬되어 있지 않은 경우")
    @Test
    void isNotOrdered() {
        assertThatThrownBy(() -> new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 3),
                new Section(LINE_ID, 2, 0)
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("두 개의 다른 lineID를 포함하는 경우")
    @Test
    void hasDifferentLineIDs() {
        assertThatThrownBy(() -> new Sections(Arrays.asList(
                new Section(LINE_ID, 1, 0),
                new Section(1234L, 2, 3)
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
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(lowermostSection.getStationID(), LOWER_THAN_LOWERMOST_ID, 3));

        Section newSection = sections.getNewSectionIfValid(createValue);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, LOWER_THAN_LOWERMOST_ID, 9));
        assertThat(newSection.getDistance()).isEqualTo(9);
    }

    @DisplayName("상행종점이 새로운 역")
    @Test
    void insertNewUppermostSection() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(UPPER_THAN_UPPERMOST_ID, uppermostSection.getStationID(), 3));

        Section newSection = sections.getNewSectionIfValid(createValue);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, UPPER_THAN_UPPERMOST_ID, -5));
        assertThat(newSection.getDistance()).isEqualTo(-5);
    }

    @DisplayName("중간 상행점에 Section을 삽입")
    @Test
    void insertNewInMiddleLeft() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(MIDDLE_LEFT_ID, middleSection.getStationID(), 3));

        Section newSection = sections.getNewSectionIfValid(createValue);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_LEFT_ID, -1));
        assertThat(newSection.getDistance()).isEqualTo(-1);
    }

    @DisplayName("중간 하행점에 Section을 삽입")
    @Test
    void insertNewInMiddleRight() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(middleSection.getStationID(), MIDDLE_RIGHT_ID, 3));

        Section newSection = sections.getNewSectionIfValid(createValue);

        assertThat(newSection).isEqualTo(new Section(LINE_ID, MIDDLE_RIGHT_ID, 5));
        assertThat(newSection.getDistance()).isEqualTo(5);
    }

    @DisplayName("지하철 노선에 이미 등록되어있는 역을 등록한다")
    @Test
    void bothSectionAlreadyExists() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(middleSection.getStationID(), lowermostSection.getStationID(), 3));

        assertThatThrownBy(() -> sections.getNewSectionIfValid(createValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already");
    }

    @DisplayName("지하철 노선에 등록되지 않은 역을 기준으로 등록한다")
    @Test
    void noneOfSectionsExists() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(MIDDLE_LEFT_ID, MIDDLE_RIGHT_ID, 2));

        assertThatThrownBy(() -> sections.getNewSectionIfValid(createValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("neither");
    }

    @DisplayName("추가하려는 노선이 기존 노선의 거리 이상이다")
    @Test
    void invalidDistanceIsNotInsertable() {
        SectionCreateValue createValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(middleSection.getStationID(), MIDDLE_RIGHT_ID, 4));

        assertThatThrownBy(() -> sections.getNewSectionIfValid(createValue))
                .isInstanceOf(IllegalArgumentException.class);
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
        SectionCreateValue value = new SectionCreateValue(LINE_ID,
                new SectionRequest(middleSection.getStationID(), MIDDLE_LEFT_ID, 0));

        assertThatCode(() -> sections.checkAreValidStationIDs(value)).doesNotThrowAnyException();
    }

    @DisplayName("구간에 추가할 역들이 모두 존재하거나 모두 존재하지 않을땐 삽입 불가능하다")
    @Test
    void testAreNotInsertableSections() {
        SectionCreateValue bothExistingSectionsValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(uppermostSection.getStationID(), middleSection.getStationID(), 0));
        SectionCreateValue nonExistingSectionsValue = new SectionCreateValue(LINE_ID,
                new SectionRequest(LOWER_THAN_LOWERMOST_ID, MIDDLE_LEFT_ID, 0));

        assertThatThrownBy(() -> sections.checkAreValidStationIDs(bothExistingSectionsValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already");
        assertThatThrownBy(() -> sections.checkAreValidStationIDs(nonExistingSectionsValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("neither");
    }

    @DisplayName("추가할 구간의 거리가 유효하다")
    @ParameterizedTest
    @ValueSource(ints = {5, -1})
    void testHaveValidDistance(int distance) {
        assertThatNoException().isThrownBy(() -> sections.checkIsValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_RIGHT_ID, distance)
        ));
    }

    @DisplayName("추가할 구간의 거리가 유효하지 않다")
    @ParameterizedTest
    @ValueSource(ints = {6, -2})
    void testHaveInvalidDistance(int distance) {
        assertThatThrownBy(() -> sections.checkIsValidDistance(
                middleSection,
                new Section(LINE_ID, MIDDLE_RIGHT_ID, distance)
        )).isInstanceOf(IllegalArgumentException.class);
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

        assertThat(sections.findSectionBy(middleSection.getStationID())).isEqualTo(middleSection);
        assertThat(sections.findSectionBy(nonExistingSection.getStationID())).isNull();
    }
}
