package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.SectionsInLine;
import subway.domain.Line;
import subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectionsInLineTest {
    Line 이호선 = new Line(1L, "이호선", "그린");
    Station 사당역 = new Station(1L, "사당역");
    Station 방배역 = new Station(2L, "방배역");
    Station 서초역 = new Station(3L, "서초역");
    Station 교대역 = new Station(4L, "교대역");
    Station 판교역 = new Station(5L, "판교역");
    Section 사당_방배 = new Section(2L, 이호선, 사당역, 방배역, 10);
    Section 방배_서초 = new Section(1L, 이호선, 방배역, 서초역, 20);
    Section 서초_교대 = new Section(3L, 이호선, 서초역, 교대역, 10);
    SectionsInLine 서초_교대_구간;

    @BeforeEach
    void setUp() {
        List<Section> sections = new ArrayList<>();
        sections.add(방배_서초);
        sections.add(사당_방배);
        sections.add(서초_교대);
        서초_교대_구간 = new SectionsInLine(sections);
    }

    @DisplayName("구간이 역을 포함하는지 테스트")
    @Test
    void testContainStation() {
        assertTrue(서초_교대_구간.containsStation(서초역));
        assertFalse(서초_교대_구간.containsStation(판교역));
    }

    @DisplayName("역의 순서를 테스트")
    @Test
    void testStationSequence() {
        List<Station> stations = 서초_교대_구간.findSortedStations();
        assertEquals(사당역, stations.get(0));
        assertEquals(방배역, stations.get(1));
        assertEquals(서초역, stations.get(2));
        assertEquals(교대역, stations.get(3));
    }

    @DisplayName("종점을 테스트")
    @Test
    void testTerminal() {
        Station 상행종점 = 서초_교대_구간.findUpTerminalSection().getUpStation();
        assertEquals(사당역, 상행종점);
        Station 하행종점 = 서초_교대_구간.findDownTerminalSection().getDownStation();
        assertEquals(교대역, 하행종점);

        assertTrue(서초_교대_구간.ofTerminalStationIs(사당역));
        assertTrue(서초_교대_구간.ofTerminalStationIs(교대역));
   }

    @DisplayName("역이 속한 구간을 테스트")
    @Test
    void testContainingSection() {
        Section 도착지_방배역_구간 = 서초_교대_구간.findUpwardSectionByStation(방배역);
        assertEquals(사당_방배, 도착지_방배역_구간);
        Section 출발지_방배역_구간 = 서초_교대_구간.findDownWardSectionByStation(방배역);
        assertEquals(방배_서초, 출발지_방배역_구간);
    }
}