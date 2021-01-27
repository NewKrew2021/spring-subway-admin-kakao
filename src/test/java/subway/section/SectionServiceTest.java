package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import subway.exception.InvalidSectionException;
import subway.line.Line;
import subway.line.LineService;
import subway.station.Station;
import subway.station.StationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        station1 = stationService.createStation(new Station("교대역"));
        station2 = stationService.createStation(new Station("강남역"));
        station3 = stationService.createStation(new Station("역삼역"));
        line = lineService.createLineAndSection(new Line("2호선", "green", station1.getId(), station2.getId()), 10);
    }

    @Test
    @DisplayName("새로운 구간을 추가한다(교대-강남-역삼)")
    void addSection_success1() {
        // given
        Section section = new Section(station2.getId(), station3.getId(), 10, line.getId());

        // when
        sectionService.addSection(line.getId(), section);

        // then
        assertThat(lineService.getLine(line.getId()).getSections().getStationIds()).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("새로운 구간을 추가한다(역삼-교대-강남)")
    void addSection_success2() {
        // given
        Section section = new Section(station3.getId(), station1.getId(), 10, line.getId());

        // when
        sectionService.addSection(line.getId(), section);

        // then
        assertThat(lineService.getLine(line.getId()).getSections().getStationIds()).containsExactly(3L, 1L, 2L);
    }

    @Test
    @DisplayName("기존 구간과 거리가 같은 구간 추가했을 때 에러 발생")
    void addSection_whenInvalidDistance() {
        // given
        Section section = new Section(station1.getId(), station3.getId(), 10, line.getId());

        assertThatExceptionOfType(InvalidSectionException.class).isThrownBy(() -> {
            sectionService.addSection(line.getId(), section);
        }).withMessageMatching("구간의 길이는 0보다 커야 합니다.");
    }
}
