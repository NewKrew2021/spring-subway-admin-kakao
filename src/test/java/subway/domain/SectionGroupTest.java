package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.SectionGroup;
import subway.exception.NoContentException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionGroupTest {

    @Test
    @DisplayName("처음으로 구간을 넣었을 때 3개의 구간이 포함되어 있는지 확인한다.")
    void insertFirstSection() {
        SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);

        assertThat(sections.getSections().size()).isEqualTo(3);

        assertThat(sections.getSections().get(1)).isEqualTo(new Section(1L, 2L, 3L, 10));
    }

    @Test
    @DisplayName("기존 노선보다 큰 길이를 갖는 노선을 넣을 때 예외가 발생한다.")
    void insertSectionWithLargerDistance() {
        assertThatThrownBy(() -> {
            SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);
            sections.insertSection(1L, 2L, 4L, 20);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존 노선과 겹치지 않는 노선을 넣으면 예외가 발생한다.")
    void insertSectionWithNoDuplicate() {
        assertThatThrownBy(() -> {
            SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);
            sections.insertSection(1L, 4L, 5L, 20);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존 노선과 겹치지 않는 노선을 넣으면 예외가 발생한다.")
    void insertSectionWithAllDuplicate() {
        assertThatThrownBy(() -> {
            SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);
            sections.insertSection(1L, 2L, 3L, 5);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간을 나눌 때 나눠진 구간이 제대로 구해지는지 확인한다.")
    void divideSection() {
        SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);
        Section insertedSection = sections.insertSection(1L, 2L, 4L, 4);

        Section dividedSection = sections.divideSection(insertedSection);

        assertThat(dividedSection).isEqualTo(new Section(1L, 4L, 3L, 6));
    }

    @Test
    @DisplayName("구간이 한 개 이하일 때 예외가 발생하는지 확인한다.")
    void deleteStationWithLessThanOneSection() {
        assertThatThrownBy(() -> {
            SectionGroup sections = SectionGroup.insertFirstSection(1L, 2L, 3L, 10);
            sections.deleteStation(2L);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 내 없는 지하철 역을 삭제할 때 예외가 발생하는지 확인한다.")
    void deleteNonExistence() {
        assertThatThrownBy(() -> {
            SectionGroup sections = new SectionGroup(Arrays.asList(
                    new Section(1L, Section.VIRTUAL_ENDPOINT_ID, 1L, 10),
                    new Section(1L, 1L, 3L, 10),
                    new Section(1L, 3L, 2L, 10),
                    new Section(1L, 2L, Section.VIRTUAL_ENDPOINT_ID, 10)));

            sections.deleteStation(5L);
        }).isInstanceOf(NoContentException.class);
    }

    @Test
    @DisplayName("역이 삭제된 이후에 합쳐진 구간이 제대로 구해졌는지 확인한다.")
    void combineSection() {
        SectionGroup sections = new SectionGroup(Arrays.asList(
                new Section(1L, Section.VIRTUAL_ENDPOINT_ID, 1L, 10),
                new Section(1L, 1L, 3L, 10),
                new Section(1L, 3L, 2L, 10),
                new Section(1L, 2L, Section.VIRTUAL_ENDPOINT_ID, 10)));

        Section deletedSection = sections.deleteStation(3L);
        Section combinedSection = sections.combineSection(deletedSection);

        assertThat(combinedSection).isEqualTo(new Section(1L, 1L, 2L, 20));
    }

    @Test
    @DisplayName("Station 목록이 제대로 구해지는지 확인한다.")
    void getAllStationId() {
        SectionGroup sections = new SectionGroup(Arrays.asList(
                new Section(1L, Section.VIRTUAL_ENDPOINT_ID, 1L, 10),
                new Section(1L, 1L, 3L, 10),
                new Section(1L, 3L, 2L, 10),
                new Section(1L, 2L, Section.VIRTUAL_ENDPOINT_ID, 10)));

        assertThat(sections.getAllStationId()).isEqualTo(Arrays.asList(1L, 3L, 2L));
    }
}