package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import subway.domain.Section;
import subway.domain.Line;
import subway.domain.Station;

@DisplayName("섹션 테스트")
public class SectionTest {
    Line 이호선 = new Line(1L, "이호선", "그린");
    Station 사당역 = new Station(1L, "사당역");
    Station 방배역 = new Station(2L, "방배역");
    Station 서초역 = new Station(3L, "서초역");
    Station 교대역 = new Station(4L, "교대역");
    Section 사당_방배 = new Section(1L, 이호선, 사당역, 방배역, 10);

    @BeforeEach
    void setUp(){

    }

    @DisplayName("구간이 역을 포함하고 있는지 확인")
    @Test
    void testContainStation() {
        assertTrue(사당_방배.hasStation(사당역));
        assertTrue(사당_방배.hasStation(방배역));
        assertFalse(사당_방배.hasStation(서초역));
    }

    @DisplayName("구간이 나뉘어지는지 확인")
    @Test
    void testSplit() {
        Section 사당_서초 = new Section(2L, 이호선, 사당역, 서초역, 20);
        Section 방배_서초 = 사당_서초.splitBy(사당_방배);

        assertEquals(방배역, 방배_서초.getUpStation());
        assertEquals(서초역, 방배_서초.getDownStation());
        assertEquals(10, 방배_서초.getDistance());
    }

    @DisplayName("구간이 합쳐지는지 확인")
    @Test
    void testConnect() {
        Section 방배_서초 = new Section(2L, 이호선, 방배역, 서초역, 20);
        Section 사당_서초 = 사당_방배.connectDownward(방배_서초);

        assertEquals(사당역, 사당_서초.getUpStation());
        assertEquals(서초역, 사당_서초.getDownStation());
        assertEquals(30, 사당_서초.getDistance());

    }
}
