package subway.section.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 구간 관련 기능")
class LineSectionsTest {

    @DisplayName("한 노선에 연결되지 않은 구간들이 주어지면, 에러를 던진다.")
    @Test
    void isNotUnited() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(2L, 3L, 4L, 2);

        // then
        assertThatThrownBy(() -> new LineSections(Arrays.asList(first, second, third)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("한 줄기로 이어지지 않은 구간 리스트를 입력받을 수 없습니다.");
    }

    @DisplayName("한 노선에 연결된 구간들이 주어지면, 그 구간들의 병합 구간을 생성할 수 있다.")
    @Test
    void getMergedSection() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 3L, 4L, 2);
        LineSections lineSections = new LineSections(Arrays.asList(first, second, third));

        // then
        assertThat(lineSections.getMergedSection())
                .isEqualTo(new Section(1L, 1L, 4L, 11));
    }

    @DisplayName("단일 구간으로 이루어지면 삭제할 수 없다.")
    @Test
    void isNotDeletable() {
        // given
        LineSections lineSections = new LineSections(
                Collections.singletonList(new Section(1L, 1L, 2L, 10))
        );

        // then
        assertThat(lineSections.isNotDeletable()).isTrue();
    }

    @DisplayName("다중 구간으로 이루어지면 삭제할 수 있다.")
    @Test
    void isNotDeletable2() {
        // given
        Section first = new Section(1L, 1L, 2L, 10);
        Section second = new Section(1L, 2L, 3L, 4);
        LineSections lineSections = new LineSections(
                Arrays.asList(first, second)
        );

        // then
        assertThat(lineSections.isNotDeletable()).isFalse();
    }

    @DisplayName("구간을 삽입할 때 사이클을 형성하지 않고 연결되어 있다면, 삽입(확장) 가능하다.")
    @Test
    void isExtendable() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 4L, 1L, 2);
        LineSections lineSections = new LineSections(Arrays.asList(first, second));

        // then
        assertThat(lineSections.isExtendable(third)).isTrue();
    }

    @DisplayName("구간을 삽입할 때 사이클을 형성하면, 삽입(확장) 불가능하다.")
    @Test
    void isExtendable2() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 3L, 1L, 2);
        LineSections lineSections = new LineSections(Arrays.asList(first, second));

        // then
        assertThat(lineSections.isExtendable(third)).isFalse();
    }

    @DisplayName("구간을 삽입할 때 연결되어 있지 않으면, 삽입(확장) 불가능하다.")
    @Test
    void isExtendable3() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 4L, 5L, 2);
        LineSections lineSections = new LineSections(Arrays.asList(first, second));

        // then
        assertThat(lineSections.isExtendable(third)).isFalse();
    }

    @DisplayName("삽입(축소) 가능한 구간이 있다면, 해당 구간을 반환한다.")
    @Test
    void findCollapsibleSection() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 2L, 4L, 2);
        LineSections lineSections = new LineSections(Arrays.asList(first, second));

        // then
        assertThat(lineSections.findCollapsibleSection(third)).isPresent()
                .get()
                .isEqualTo(second);
    }

    @DisplayName("지하철 역 아이디 목록이 주어지면, 해당 아이디를 갖는 섹션만 반환한다.")
    @Test
    void filterByStationId() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 3L, 4L, 2);
        LineSections firstToThird = new LineSections(Arrays.asList(first, second, third));

        // then
        assertThat(firstToThird.filterByStationId(1L)).isEqualTo(new LineSections(Collections.singletonList(first)));
        assertThat(firstToThird.filterByStationId(2L)).isEqualTo(new LineSections(Arrays.asList(first, second)));
        assertThat(firstToThird.filterByStationId(3L)).isEqualTo(new LineSections(Arrays.asList(second, third)));
        assertThat(firstToThird.filterByStationId(4L)).isEqualTo(new LineSections(Collections.singletonList(third)));
    }

    @DisplayName("구간들이 갖고있는 구간 아이디를 반환한다.")
    @Test
    void getSectionIds() {
        // given
        Section first = new Section(1L, 1L, 1L, 2L, 3);
        Section second = new Section(2L, 1L, 2L, 3L, 6);
        Section third = new Section(3L, 1L, 3L, 4L, 2);
        LineSections firstToThird = new LineSections(Arrays.asList(first, second, third));

        // then
        assertThat(firstToThird.getSectionIds()).isEqualTo(Arrays.asList(1L, 2L, 3L));
    }

    @DisplayName("구간들이 갖고있는 지하철 역 아이디를 유니크하게 반환한다.")
    @Test
    void getStationIds() {
        // given
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 6);
        Section third = new Section(1L, 3L, 4L, 2);
        LineSections firstToThird = new LineSections(Arrays.asList(first, second, third));

        // then
        assertThat(firstToThird.getStationIds()).isEqualTo(Arrays.asList(1L, 2L, 3L, 4L));
    }
}
