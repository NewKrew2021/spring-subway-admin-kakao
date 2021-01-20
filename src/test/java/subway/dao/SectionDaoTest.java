package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.SectionDao;
import subway.domain.section.Section;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE section(\n" +
                "    id bigint auto_increment not null,\n" +
                "    line_id bigint not null,\n" +
                "    station_id bigint not null,\n" +
                "    distance int,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("해당 노선에 대한 구간들을 상행-하행 순으로 출력한다.")
    public void findAllStation() {
        //given
        sectionDao.save(new Section(1L, 10, 2L));
        sectionDao.save(new Section(2L, -5, 2L));
        sectionDao.save(new Section(3L, 62, 2L));
        sectionDao.save(new Section(4L, -75, 2L));
        sectionDao.save(new Section(5L, 2563, 2L));

        //when
        List<Section> sections = sectionDao.findAllStationsByLineId(2L);

        //then
        assertThat(sections.stream().map(section -> section.getStationId())
                .collect(Collectors.toList()))
                .isEqualTo(Arrays.asList(4L, 2L, 1L, 3L, 5L));

    }

    @Test
    @DisplayName("구간을 stationId값으로 삭제한다.")
    public void deleteById() {
        //given
        sectionDao.save(new Section(1L, 10, 2L));

        //when
        sectionDao.deleteByStationId(1L);

        //then
        assertThat(sectionDao.findAllStationsByLineId(2L).size()).isZero();
    }

    @Test
    @DisplayName("구간을 lineId값으로 삭제한다.")
    public void deleteAllSectionsByLineId() {
        //given
        sectionDao.save(new Section(1L, 10, 2L));
        sectionDao.save(new Section(2L, 20, 2L));
        sectionDao.save(new Section(3L, 30, 2L));

        //when
        sectionDao.deleteAllSectionsByLineId(2L);

        //then
        assertThat(sectionDao.findAllStationsByLineId(2L).size()).isZero();
    }

    @Test
    @DisplayName("stationId 값으로 구간을 찾는다.")
    public void findByStationId() {
        sectionDao.save(new Section(1L, 10, 2L));
        sectionDao.save(new Section(1L,20,3L));

        assertThat(sectionDao.findByStationId(1L).size()).isEqualTo(2);
    }


}