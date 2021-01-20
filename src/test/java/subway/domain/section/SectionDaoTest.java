package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import subway.dao.SectionDao;
import subway.domain.station.Station;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest
public class SectionDaoTest {
    private final Station 강남역 = new Station(1L);
    private final Station 역삼역 = new Station(2L);
    private final Station 광교역 = new Station(3L);

    private final Section 섹션1 = new Section(1L, 강남역, 역삼역, 3);
    private final Section 섹션2 = new Section(1L, 역삼역, 광교역, 4);
    private final Section 섹션3 = new Section(2L, 역삼역, 광교역, 5);

    @Autowired
    SectionDao sectionDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

//    @AfterEach
    @BeforeEach
    public void dropTable() {
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");
        jdbcTemplate.execute("create table if not exists SECTION\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    line_id bigint not null,\n" +
                "    up_station_id bigint not null,\n" +
                "    down_station_id bigint not null,\n" +
                "    distance int,\n" +
                "    primary key(id)\n" +
                ");");
    }

    @Test
    public void save() {
        assertThat(sectionDao.save(섹션1)).isEqualTo(섹션1);
    }

    @Test
    public void getByLineId() {
        sectionDao.save(섹션1);
        sectionDao.save(섹션2);
        sectionDao.save(섹션3);
        assertThat(sectionDao.getByLineId(1L)).containsExactlyElementsOf(Arrays.asList(섹션1, 섹션2));
        assertThat(sectionDao.getByLineId(2L)).containsExactlyElementsOf(Arrays.asList(섹션3));
        assertThat(sectionDao.getByLineId(3L)).containsExactlyElementsOf(Collections.emptyList());
    }

    @Test
    public void deleteById() {
        sectionDao.save(섹션1);
        assertThat(sectionDao.deleteById(1L)).isTrue();
        assertThat(sectionDao.deleteById(1L)).isFalse();
    }
}
