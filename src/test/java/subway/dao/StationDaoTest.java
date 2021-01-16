package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Station;
import subway.utils.TableRefresher;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철역 데이터 엑세스 관련 기능")
@SpringBootTest
public class StationDaoTest {
    private final Station 강남역 = new Station("강남역");
    private final Station 역삼역 = new Station("역삼역");
    private final Station 서현역 = new Station("서현역");

    @Autowired
    StationDao stationDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void refreshStation() {
        TableRefresher.refreshStation(jdbcTemplate);
    }

    @DisplayName("데이터베이스의 지하철역을 생성한다.")
    @Test
    public void saveTest() {
        assertThat(stationDao.save(강남역)).isEqualTo(강남역);
        assertThatThrownBy(() -> stationDao.save(강남역)).isInstanceOf(DataAccessException.class);
    }

    @DisplayName("데이터베이스의 지하철역을 조회한다.")
    @Test
    public void getByIdTest() {
        stationDao.save(강남역);
        assertThat(stationDao.getById(1L)).isEqualTo(강남역);
    }

    @DisplayName("데이터베이스의 여러 지하철역을 조회한다.")
    @Test
    public void batchGetByIdTest() {
        stationDao.save(강남역);
        stationDao.save(역삼역);
        stationDao.save(서현역);
        assertThat(stationDao.batchGetByIds(Arrays.asList(1L, 2L))).isEqualTo(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("데이터베이스의 지하철역 목록을 조회한다.")
    @Test
    public void findAllTest() {
        stationDao.save(강남역);
        stationDao.save(역삼역);
        assertThat(stationDao.findAll()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("데이터베이스의 지하철역을 제거한다.")
    @Test
    public void deleteByIdTest() {
        stationDao.save(강남역);
        assertThat(stationDao.deleteById(1L)).isTrue();
        assertThat(stationDao.deleteById(1L)).isFalse();
    }
}
