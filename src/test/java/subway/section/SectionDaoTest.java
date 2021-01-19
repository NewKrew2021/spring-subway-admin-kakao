package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

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
                "    up_station_id bigint not null,\n" +
                "    down_station_id bigint not null,\n" +
                "    distance int,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("상행역과 하행역의 id, 그리고 노선의 id로 db 내에 존재하는 구간을 찾는다.")
    public void findByUpStationAndDownStation_ifExist() {
        Section newSection = sectionDao.save(new Section(1L,2L,10,1L));
        assertThat(sectionDao.findByUpStationIdAndLineId(1L,1L).getId()).isEqualTo(newSection.getId());
        assertThat(sectionDao.findByDownStationIdAndLineId(2L,1L).getId()).isEqualTo(newSection.getId());
    }

    @Test
    @DisplayName("상행역과 하행역의 id, 그리고 노선의 id로 db 내에 존재하지 않는 구간을 찾는다.")
    public void findByUpStationAndDownStation_ifNotExist() {
        assertThat(sectionDao.findByUpStationIdAndLineId(1L,1L)).isEqualTo(null);
        assertThat(sectionDao.findByDownStationIdAndLineId(2L,1L)).isEqualTo(null);
    }

    @Test
    @DisplayName("구간을 업데이트 한다.")
    public void updateSection() {
        Section section = sectionDao.save(new Section(1L,2L,10,1L));
        Section expected = new Section(3L,4L,1000,2L);
        sectionDao.updateSection(section.getId(),expected);
        assertThat(sectionDao.findByUpStationIdAndLineId(3L,2L).getDistance()).isEqualTo(1000);
    }

    @Test
    @DisplayName("구간을 id값으로 삭제한다.")
    public void deleteById() {
        Section section = sectionDao.save(new Section(1L,2L,10,1L));
        sectionDao.deleteById(section.getId());
        assertThat(sectionDao.findByUpStationIdAndLineId(1L,1L)).isNull();
    }

}