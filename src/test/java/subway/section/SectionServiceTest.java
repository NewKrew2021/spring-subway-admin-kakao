package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static subway.section.Section.INF;
import static subway.section.Section.TERMINAL_ID;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private SectionService sectionService;

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(
                Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 10),
                        new Section(2L, 1L, 2L, 3L, 10),
                        new Section(3L, 1L, TERMINAL_ID, 1L, INF),
                        new Section(4L, 1L, 3L, TERMINAL_ID, 10)
                )
        );
    }

    @DisplayName("Sections가 주어지면 해당 구간들을 모두 저장한다")
    @Test
    void save() {
        // when
        sectionService.save(sections);

        // then
        verify(sectionDao, times(sections.getSections().size())).save(any(Section.class));
    }

    @DisplayName("노선의 id가 주어지면 해당 노선의 역들을 반환한다")
    @Test
    void findStationsOf() {
        // given
        long lineId = 1L;
        given(sectionDao.findByLineId(lineId)).willReturn(sections.getSections());
        given(stationDao.findById(anyLong())).willAnswer(this::createStationFromGivenId);

        // when
        List<StationResponse> responses = sectionService.findStationsOf(lineId);

        // then
        assertThat(responses)
                .extracting(StationResponse::getId)
                .containsExactly(1L, 2L, 3L);
    }

    private Optional<Station> createStationFromGivenId(InvocationOnMock method) {
        Long id = method.getArgument(0, Long.class);
        return Optional.of(new Station(id, String.format("역 %d", id)));
    }

    @DisplayName("새로 생성할 구간의 노선id, 상/하행역의 id과 거리가 주어지면 기존 구간을 지우고 나뉜 두 구간을 저장한다")
    @Test
    void createSection() {
        // given
        long lineId = 1L;
        SectionRequest request = new SectionRequest(2L, 4L, 5);
        given(sectionDao.findByLineId(lineId)).willReturn(sections.getSections());

        // when
        sectionService.createSection(lineId, request);

        // then
        verify(sectionDao).delete(refEq(new Section(2L, 1L, 2L, 3L, 10)));
        verify(sectionDao).save(refEq(new Section(1L, 2L, 4L, 5)));
        verify(sectionDao).save(refEq(new Section(1L, 4L, 3L, 5)));
    }

    @DisplayName("제거할 노선과 역의 id가 주어지면 역이 포함 되는 두 구간을 지우고, 그 구간을 합친 구간을 새로 저장한다")
    @Test
    void removeSection() {
        // given
        long lineId = 1L;
        long stationId = 1L;
        given(sectionDao.findByLineId(lineId)).willReturn(sections.getSections());

        // when
        sectionService.removeSection(lineId, stationId);

        // then
        verify(sectionDao).delete(refEq(new Section(3L, 1L, TERMINAL_ID, 1L, INF)));
        verify(sectionDao).delete(refEq(new Section(1L, 1L, 1L, 2L, 10)));
        verify(sectionDao).save(refEq(new Section(1L, TERMINAL_ID, 2L, INF)));
    }
}
