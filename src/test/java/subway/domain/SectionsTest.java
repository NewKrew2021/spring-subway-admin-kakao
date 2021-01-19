package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간들의 테스트")
public class SectionsTest {
    private Sections sections;

    @BeforeEach
    public void setUp() {
        sections = new Sections(
                Arrays.asList(new Section(1L, 1L, 2L, 3),
                        new Section(1L, 3L, 4L, 3),
                        new Section(1L, 2L, 3L, 3),
                        new Section(1L, 5L, 6L, 3),
                        new Section(1L, 4L, 5L, 3)));
    }

    @DisplayName("상행 동일 구간 탐색 테스트")
    @Test
    public void getUpMatchSectionTest() {
        assertThat(sections.getUpMatchSection(2L)).isEqualTo(
                new Section(1L, 2L, 3L, 3));
    }

    @DisplayName("하행 동일 구간 탐색 테스트")
    @Test
    public void getDownMatchSectionTest() {
        assertThat(sections.getDownMatchSection(2L)).isEqualTo(
                new Section(1L, 1L, 2L, 3));
    }

    @DisplayName("없는 동일 구간 탐색 테스트")
    @Test
    public void getNoMatchSectionTest() {
        assertThat(sections.getUpMatchSection(10L)).isEqualTo(null);
    }

    @DisplayName("첫번째 역 탐색 테스트")
    @Test
    public void findFirstStationTest() {
        assertThat(sections.findFirstStation()).isEqualTo(1L);
    }

    @DisplayName("상행역을 키, 구간을 값으로 가지는 해시 생성 테스트")
    @Test
    public void generateConnectionTest() {
        Map<Long, Section> connection = new HashMap<>();
        connection.put(1L, new Section(1L, 1L, 2L, 3));
        connection.put(2L, new Section(1L, 2L, 3L, 3));
        connection.put(3L, new Section(1L, 3L, 4L, 3));
        connection.put(4L, new Section(1L, 4L, 5L, 3));
        connection.put(5L, new Section(1L, 5L, 6L, 3));
        assertThat(sections.generateConnection()).isEqualTo(connection);
    }
}
