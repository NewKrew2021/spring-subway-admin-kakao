package subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    public StationDaoTest(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Test
    @DisplayName("지하철 역이 제대로 저장되는지 확인한다.")
    void save() {
        Station savedStation = stationDao.save(new Station("강남역"));
        Station foundStation = stationDao.findOne(savedStation.getId());

        assertThat(savedStation.getId()).isEqualTo(foundStation.getId());
        assertThat(savedStation.getName()).isEqualTo(foundStation.getName());
    }

    @Test
    @DisplayName("중복된 이름으로 지하철 역을 저장할 때 예외가 발생하는지 확인한다.")
    void saveDuplicate() {
        assertThatThrownBy(() -> {
            stationDao.save(new Station("강남역"));
            stationDao.save(new Station("강남역"));
        }).isInstanceOf(DuplicateNameException.class);
    }

    @Test
    @DisplayName("목록을 조회했을 때 개수가 정확한지 확인한다.")
    void findAll() {
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("양재역"));

        assertThat(stationDao.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("삭제했을 때 더 이상 조회되지 않는 것을 확인한다.")
    void deleteById() {
        assertThatThrownBy(() -> {
            Station station = stationDao.save(new Station("강남역"));
            stationDao.deleteById(station.getId());
            stationDao.findOne(station.getId());
        }).isInstanceOf(NoContentException.class);
    }
}
