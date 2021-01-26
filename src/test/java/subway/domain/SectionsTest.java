package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.AlreadyExistDataException;
import subway.exception.DeleteImpossibleException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 리스트 유닛 테스트")
public class SectionsTest {
    Sections sections;
    Station 잠실;
    Station 분당;
    Station 판교;
    Station 정자;
    Station 야탑;
    Station 광교;

    @BeforeEach
    void setUp() {
        잠실 = new Station(1L, "잠실");
        분당 = new Station(2L, "분당");
        판교 = new Station(3L, "판교");
        정자 = new Station(4L, "정자");
        야탑 = new Station(5L, "야탑");
        광교 = new Station(6L, "광교");
        sections = new Sections(Arrays.asList(new Section(잠실, 분당, 5, 1L), new Section(분당, 판교, 5, 1L)));
    }

    @Test
    @DisplayName("섹션 탐색 테스트 - 하행")
    void findSectionByDownId() {
        assertThat(sections.findSectionByDownStation(분당)).isEqualTo(new Section(잠실, 분당, 3, 1L));
    }

    @Test
    @DisplayName("섹션 탐색 테스트 - 상행")
    void findSectionByUpId() {
        assertThat(sections.findSectionByUpStation(잠실)).isEqualTo(new Section(잠실, 분당, 3, 1L));
    }

    @Test
    @DisplayName("섹션 탐색 테스트 - 존재하지 않는 섹션 하행")
    void findSectionByDownIdNull() {
        assertThat(sections.findSectionByDownStation(잠실)).isNull();
    }

    @Test
    @DisplayName("섹션 탐색 테스트 - 존재하지 않는 섹션 상행")
    void findSectionByUpIdNull() {
        assertThat(sections.findSectionByUpStation(판교)).isNull();
    }

    @Test
    @DisplayName("역정보 가져오는 테스트")
    void getStationsTest() {
        assertThat(sections.getStations()).containsExactly(잠실, 분당, 판교);
    }

    @Test
    @DisplayName("시점 테스트")
    void getStartStationTest() {
        assertThat(sections.getStartStation()).isEqualTo(잠실);
    }

    @Test
    @DisplayName("종점 테스트")
    void getEndStationTest() {
        assertThat(sections.getEndStation()).isEqualTo(판교);
    }

    @Test
    @DisplayName("구간 추가 테스트")
    void addSectionTest() {
        sections.addSection(new Section(분당, 정자, 1, 1L));
        assertThat(sections.getStations()).containsExactly(잠실, 분당, 정자, 판교);
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 중복")
    void addSectionExceptionTest() {
        assertThatExceptionOfType(AlreadyExistDataException.class).isThrownBy(() -> sections.addSection(new Section(잠실, 분당, 3, 1L)));
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 잘못된 정보")
    void addSectionExceptionTest2() {
        assertThatExceptionOfType(IllegalStationException.class).isThrownBy(() -> sections.addSection(new Section(야탑, 광교, 3, 1L)));
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void deleteSectionTest() {
        sections.deleteSection(분당);
        assertThat(sections.getStations()).containsExactly(잠실, 판교);
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트 - 존재 섹션 1일때")
    void deleteSectionExceptionTest() {
        assertThatExceptionOfType(DeleteImpossibleException.class).isThrownBy(() -> {
            sections.deleteSection(잠실);
            sections.deleteSection(분당);
        });
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트 - 존재하지 않는 역일때")
    void deleteSectionExceptionTest2() {
        assertThatExceptionOfType(DeleteImpossibleException.class).isThrownBy(() -> {
            sections.deleteSection(광교);
        });
    }
}
