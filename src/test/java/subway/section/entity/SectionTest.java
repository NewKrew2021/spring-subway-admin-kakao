package subway.section.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionTest {

    @DisplayName("구간의 상/하행 지하철 역이 입력받은 지하철 역을 포함한다면, 참을 반환한다.")
    @Test
    void containsStation() {
        // given
        long stationId1 = 1L;
        long stationId2 = 2L;
        Section section = new Section(1L, 1L, stationId1, stationId2, 10);

        // then
        boolean isContainsStationId1 = section.containsStation(stationId1);
        boolean isContainsStationId2 = section.containsStation(stationId2);
        assertThat(isContainsStationId1).isTrue();
        assertThat(isContainsStationId2).isTrue();
    }

    @DisplayName("구간의 상/하행 지하철 역이 입력받은 지하철 역을 포함하지 않는다면, 거짓을 반환한다.")
    @Test
    void containsStation2() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 10);

        // then
        boolean isContains = section.containsStation(3L);
        assertThat(isContains).isFalse();
    }

    @DisplayName("구간의 상/하행 지하철 역 중 하나만 일치하고 거리가 더 짧은 구간이 주어지면, 축소(삽입)할 수 있다.")
    @Test
    void isCollapsible() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 4);
        Section collapsibleSection = new Section(2L, 1L, 1L, 3L, 5);

        // then
        boolean isCollapsible = collapsibleSection.isCollapsible(baseSection);
        assertThat(isCollapsible).isTrue();
    }

    @DisplayName("구간의 상/하행 지하철 역 중 하나만 일치하고 거리가 같거나 긴 구간이 주어지면, 축소(삽입)할 수 없다.")
    @Test
    void isCollapsible2() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 10);
        Section nonCollapsibleSection = new Section(2L, 1L, 1L, 3L, 5);

        // then
        boolean isCollapsible = nonCollapsibleSection.isCollapsible(baseSection);
        assertThat(isCollapsible).isFalse();
    }

    @DisplayName("구간의 상/하행 지하철 역 중 하나도 일치하지 않으면, 축소(삽입)할 수 없다.")
    @Test
    void isCollapsible3() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 4);
        Section nonCollapsibleSection = new Section(2L, 1L, 2L, 1L, 5);

        // then
        boolean isCollapsible = nonCollapsibleSection.isCollapsible(baseSection);
        assertThat(isCollapsible).isFalse();
    }

    @DisplayName("구간의 상/하행 지하철 역 중 둘 다 일치하면, 축소(삽입)할 수 없다.")
    @Test
    void isCollapsible4() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 4);
        Section nonCollapsibleSection = new Section(2L, 1L, 1L, 2L, 5);

        // then
        boolean isCollapsible = nonCollapsibleSection.isCollapsible(baseSection);
        assertThat(isCollapsible).isFalse();
    }

    @DisplayName("축소(삽입)할 수 있는 구간이 주어진다면, 축소된 구간을 반환한다.")
    @Test
    void collapse() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 4);
        Section collapsibleSection = new Section(2L, 1L, 1L, 3L, 5);

        // then
        Section collapsedSection = collapsibleSection.getCollapsedSection(baseSection);
        assertThat(collapsedSection).isEqualTo(new Section(2L, 1L, 2L, 3L, 1));
    }

    @DisplayName("축소(삽입)할 수 없는 구간이 주어진다면, 예외를 던진다.")
    @Test
    void collapse2() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 10);
        Section nonCollapsibleSection = new Section(2L, 1L, 1L, 3L, 5);

        // then
        try {
            nonCollapsibleSection.getCollapsedSection(baseSection);
        } catch (Error error) {
            assertThat(error).isInstanceOf(AssertionError.class);
        }
    }
}
