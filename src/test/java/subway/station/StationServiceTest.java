package subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import subway.exception.DuplicateNameException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @BeforeEach
    void setup() {
        stationService.createStation(new Station("강남역"));
    }

    @DisplayName("새로운 역을 추가한다.")
    @Test
    void createStation_success() {
        // when
        assertThatNoException().isThrownBy(() -> {
            stationService.createStation(new Station("역삼역"));
        });

        // then
        assertThat(stationService.getAllStations()).hasSize(2);
    }

    @DisplayName("이미 존재하는 이름의 역을 추가하면 예외 발생하고 역 추가되지 않음.")
    @Test
    void createStation_duplicateName() {
        // when
        assertThatExceptionOfType(DuplicateNameException.class).isThrownBy(() -> {
            stationService.createStation(new Station("강남역"));
        }).withMessageMatching("중복된 역 이름입니다.");

        // then
        assertThat(stationService.getAllStations()).hasSize(1);
    }
}
