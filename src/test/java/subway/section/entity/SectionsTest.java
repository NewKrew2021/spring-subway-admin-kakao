package subway.section.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 묶음 관련 기능")
class SectionsTest {

    @DisplayName("구간의 입력 순서에 상관없이, 정렬된 지하철 역 id 목록을 반환한다.")
    @Test
    void getStationIdsInDownwardOrder() {
        // given
        Section first = new Section(1L, 1L, 1L, 2L, 1);
        Section second = new Section(3L, 1L, 2L, 3L, 4);
        Section third = new Section(2L, 1L, 3L, 4L, 5);
        Sections sections = new Sections(
                Stream.of(second, third, first)
                        .collect(Collectors.toList())
        );

        // when
        List<Long> stationIds = sections.getStationIdsInDownwardOrder();

        // then
        assertThat(stationIds).isEqualTo(Arrays.asList(1L, 2L, 3L, 4L));
    }

    @DisplayName("구간의 입력 순서에 상관없이, 병합된 구간을 반환한다.")
    @Test
    void merge() {
        // given
        Section first = new Section(1L, 1L, 1L, 2L, 1);
        Section second = new Section(3L, 1L, 2L, 3L, 4);
        Section third = new Section(2L, 1L, 3L, 4L, 5);
        Sections sections = new Sections(
                Stream.of(second, third, first)
                        .collect(Collectors.toList())
        );

        // when
        Section mergedSection = sections.merge();

        // then
        assertThat(mergedSection).isEqualTo(new Section(1L, 1L, 4L, 10));
    }

    @DisplayName("구간이 한개 있을 때 삭제할 수 없다.")
    @Test
    void isNotDeletable() {
        // when
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        Sections sections = new Sections(
                Stream.of(section)
                        .collect(Collectors.toList())
        );

        // then
        boolean isNotDeletable = sections.isNotDeletable();
        assertThat(isNotDeletable).isTrue();
    }

    @DisplayName("구간이 두개 이상 있을 때 삭제할 수 있다.")
    @Test
    void isNotDeletable2() {
        // when
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Sections sections = new Sections(
                Stream.of(section1, section2)
                        .collect(Collectors.toList())
        );

        // then
        boolean isNotDeletable = sections.isNotDeletable();
        assertThat(isNotDeletable).isFalse();
    }

    @DisplayName("구간이 두개 이상 있을 때 다중이다.")
    @Test
    void isMultiple() {
        // when
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 4);
        Sections sections = new Sections(
                Stream.of(section1, section2)
                        .collect(Collectors.toList())
        );

        // then
        boolean isMultiple = sections.isMultiple();
        assertThat(isMultiple).isTrue();
    }

    @DisplayName("구간이 두개 미만 있을 때 단일이다.")
    @Test
    void isMultiple2() {
        // when
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        Sections sections = new Sections(
                Stream.of(section)
                        .collect(Collectors.toList())
        );

        // then
        boolean isMultiple = sections.isMultiple();
        assertThat(isMultiple).isFalse();
    }

    @DisplayName("상/하행 지하철 역 중 하나만 하/상행 지하철 역과 같은 구간은 확장할 수 있다.")
    @Test
    void isExtendable() {
        // given
        Section baseSection = new Section(1L, 1L, 1L, 2L, 10);
        Section rightSection = new Section(2L, 1L, 2L, 3L, 5);
        Section leftSection = new Section(3L, 1L, 4L, 1L, 2);
        Sections sections = new Sections(
                Stream.of(baseSection)
                        .collect(Collectors.toList())
        );

        // then
        boolean leftIsExtendable = sections.isExtendable(leftSection);
        boolean rightIsExtendable = sections.isExtendable(rightSection);
        assertThat(leftIsExtendable).isTrue();
        assertThat(rightIsExtendable).isTrue();
    }

    @DisplayName("순환 구간은 확장할 수 없다.")
    @Test
    void isExtendable2() {
        // given
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 1L, 5);
        Sections sections = new Sections(
                Stream.of(section1)
                        .collect(Collectors.toList())
        );

        // then
        boolean isExtendable = sections.isExtendable(section2);
        assertThat(isExtendable).isFalse();
    }

    @DisplayName("상/하행 둘다 같은 구간은 확장할 수 없다.")
    @Test
    void isExtendable3() {
        // given
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 1L, 2L, 10);
        Sections sections = new Sections(
                Stream.of(section1)
                        .collect(Collectors.toList())
        );

        // then
        boolean isExtendable = sections.isExtendable(section2);
        assertThat(isExtendable).isFalse();
    }

    @DisplayName("상/하행 둘다 다른 구간은 확장할 수 없다.")
    @Test
    void isExtendable4() {
        // given
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 3L, 4L, 10);
        Sections sections = new Sections(
                Stream.of(section1)
                        .collect(Collectors.toList())
        );

        // then
        boolean isExtendable = sections.isExtendable(section2);
        assertThat(isExtendable).isFalse();
    }

    @DisplayName("축소(새로운 구간을 삽입)할 수 있는 구간이 있으면 반환한다.")
    @Test
    void findCollapsibleSection() {
        // given
        Section first = new Section(1L, 1L, 1L, 2L, 10);
        Section second = new Section(3L, 1L, 2L, 4L, 4);
        Section third = new Section(2L, 1L, 2L, 3L, 5);
        Sections sections = new Sections(
                Stream.of(first, third)
                        .collect(Collectors.toList())
        );

        // when
        Optional<Section> collapsibleSection = sections.findCollapsibleSection(second);

        // then
        assertThat(collapsibleSection).isPresent()
                .get()
                .isEqualTo(third);
    }

    @DisplayName("축소(새로운 구간을 삽입)할 수 있는 구간이 없으면 반환하지 않는다.")
    @Test
    void findCollapsibleSection2() {
        // given
        Section first = new Section(1L, 1L, 1L, 2L, 10);
        Section second = new Section(2L, 1L, 2L, 3L, 5);
        Section third = new Section(3L, 1L, 2L, 4L, 7);
        Sections sections = new Sections(
                Stream.of(first, second)
                        .collect(Collectors.toList())
        );

        // when
        Optional<Section> collapsibleSection = sections.findCollapsibleSection(third);

        // then
        assertThat(collapsibleSection).isNotPresent();
    }
}
