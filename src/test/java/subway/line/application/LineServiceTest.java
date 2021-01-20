package subway.line.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.line.application.LineService;
import subway.line.domain.Line;
import subway.line.domain.LineDao;
import subway.line.presentation.LineRequest;
import subway.line.presentation.LineResponse;
import subway.section.application.SectionService;
import subway.station.domain.Station;
import subway.station.presentation.StationResponse;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("이름과 색깔이 주어지면 노선을 생성한다")
    @Test
    void create() {
        // given
        long lineId = 1L;
        LineRequest request = new LineRequest("1호선", "BLUE", 1L, 2L, 10);
        given(lineDao.existBy(request.getName())).willReturn(false);
        given(lineDao.save(any(Line.class))).willReturn(request.toEntity(lineId));
        given(sectionService.getStationsOf(lineId)).willReturn(Arrays.asList(
                StationResponse.from(new Station(1L, "의정부역")),
                StationResponse.from(new Station(2L, "시청역"))
        ));

        // when
        LineResponse result = lineService.create(request);

        // then
        verify(sectionService).getStationsOf(lineId);
        assertAll(
                () -> assertThat(result).usingRecursiveComparison()
                        .ignoringFields("stations")
                        .isEqualTo(request.toEntity(lineId)),
                () -> assertThat(result.getStations()).hasSize(2)
        );
    }

    @DisplayName("노선 생성 시, 이름이 중복된 노선이라면 예외가 발생한다")
    @Test
    void createFail() {
        // given
        LineRequest request = new LineRequest("1호선", "BLUE", 1L, 2L, 10);
        given(lineDao.existBy(request.getName())).willReturn(true);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> lineService.create(request))
                .withMessage("이미 등록된 지하철 노선 입니다.");
    }

    @DisplayName("id로 노선을 찾는다")
    @Test
    void findById() {
        // given
        long lineId = 1L;
        Line savedLine = new Line(lineId, "1호선", "BLUE");
        given(lineDao.findById(lineId)).willReturn(Optional.of(savedLine));
        given(sectionService.getStationsOf(lineId)).willReturn(Arrays.asList(
                StationResponse.from(new Station(1L, "의정부역")),
                StationResponse.from(new Station(2L, "시청역"))
        ));

        // when
        LineResponse result = lineService.findBy(lineId);

        // then
        assertAll(
                () -> assertThat(result).usingRecursiveComparison()
                        .ignoringFields("stations")
                        .isEqualTo(savedLine),
                () -> assertThat(result.getStations()).hasSize(2)
        );
    }
}
