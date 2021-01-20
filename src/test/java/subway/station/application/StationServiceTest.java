package subway.station.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.station.application.StationService;
import subway.station.domain.Station;
import subway.station.domain.StationDao;
import subway.station.presentation.StationRequest;
import subway.station.presentation.StationResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private StationService stationService;

    @DisplayName("역의 이름이 주어지면 역을 생성한다")
    @Test
    void save() {
        // given
        String stationName = "분당역";
        StationRequest request = new StationRequest(stationName);
        given(stationDao.existsBy(stationName)).willReturn(false);
        Station newStation = new Station(1L, stationName);
        given(stationDao.save(any(Station.class))).willReturn(newStation);

        // when
        StationResponse response = stationService.create(request);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(newStation);
    }


    @DisplayName("이미 존재하는 이름의 지하철 역을 생성하면 지하철 역 생성에 실패한다")
    @Test
    void saveFail() {
        // given
        String stationName = "분당역";
        StationRequest request = new StationRequest(stationName);
        given(stationDao.existsBy(stationName)).willReturn(true);

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> stationService.create(request))
                .withMessage("이미 등록된 지하철역 입니다.");
    }

    @DisplayName("저장된 역을 모두 찾는다")
    @Test
    void findAll() {
        // given
        List<Station> savedStations = Arrays.asList(
                new Station(1L, "분당역"),
                new Station(2L, "서현역"),
                new Station(4L, "오금역"),
                new Station(5L, "수서역")
        );
        given(stationDao.findAll()).willReturn(savedStations);

        // when
        List<StationResponse> result = stationService.findAll();

        // then
        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(savedStations);
    }

    @DisplayName("id에 맞는 역을 삭제한다")
    @Test
    void delete() {
        // given
        Long stationId = 1L;

        // when
        stationService.delete(stationId);

        // then
        verify(stationDao).deleteById(stationId);
    }
}
