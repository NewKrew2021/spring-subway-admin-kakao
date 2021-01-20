package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.station.Station;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

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

        jdbcTemplate.execute("DROP TABLE station IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE station(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("구간을 생성한다.")
    public void createSection() {
        Section newSection = sectionService.createSection(1L,10,2L);
        assertThat(newSection.getDistance()).isEqualTo(10);
        assertThat(newSection.getStationId()).isEqualTo(1L);
        assertThat(newSection.getLineId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("해당 노선의 모든 구간을 노선순서대로 가져온다.")
    public void getStationOfLine() {
        //given
        stationService.createStation("광교");
        stationService.createStation("광교중앙");
        stationService.createStation("상현");
        stationService.createStation("수지구청");

        sectionService.createSection(4L,352,2L);
        sectionService.createSection(2L,0,2L);
        sectionService.createSection(3L,142,2L);
        sectionService.createSection(1L,-53,2L);

        assertThat(sectionService.getStationsOfLine(2L).stream()
                .map(Station::getId)
                .collect(Collectors.toList())).isEqualTo(Arrays.asList(1L,2L,3L,4L));
    }

    @Test
    @DisplayName("구간을 추가한다")
    public void addSection() {
        //given
        stationService.createStation("광교");
        stationService.createStation("광교중앙");
        stationService.createStation("상현");
        stationService.createStation("수지구청");

        sectionService.createSection(4L,512,2L);
        sectionService.createSection(3L, 142,2L);
        sectionService.createSection(1L,-53,2L);

        //when
        sectionService.addSection(2L, 1L,2L,50);


        //then
        assertThat(sectionService.getStationsOfLine(2L).stream()
                .map(Station::getId)
                .collect(Collectors.toList())).isEqualTo(Arrays.asList(1L,2L,3L,4L));
    }

    @Test
    @DisplayName("구간을 삭제한다")
    public void deleteSection() {
        //given
        stationService.createStation("광교");
        stationService.createStation("광교중앙");
        stationService.createStation("상현");

        sectionService.createSection(2L,512,2L);
        sectionService.createSection(3L, 142,2L);
        sectionService.createSection(1L,-53,2L);

        //when
        sectionService.deleteSection(2L,3L);

        //then
        assertThat(sectionService.getStationsOfLine(2L).stream()
                .map(Station::getId)
                .collect(Collectors.toList())).isEqualTo(Arrays.asList(1L,2L));
    }

    @Test
    @DisplayName("해당 라인의 모든 구간을 삭제한다.")
    public void deleteAllSections() {
        //given
        stationService.createStation("광교");
        stationService.createStation("광교중앙");
        stationService.createStation("상현");

        sectionService.createSection(2L,512,2L);
        sectionService.createSection(3L, 142,2L);
        sectionService.createSection(1L,-53,2L);

        //when
        sectionService.deleteAllSectionsOfLine(2L);

        //then
        assertThat(sectionService.getStationsOfLine(2L).size()).isZero();

    }

}
