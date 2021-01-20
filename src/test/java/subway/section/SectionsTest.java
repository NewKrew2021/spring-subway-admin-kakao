package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.exceptions.sectionExceptions.SectionDeleteException;
import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.exceptions.sectionExceptions.SectionNoStationException;
import subway.exceptions.sectionExceptions.SectionSameSectionException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void create() {
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(new Section(1L,1L,0));
        tempSections.add(new Section(1L,3L,10));
        tempSections.add(new Section(1L,2L,5));
        sections = new Sections(tempSections);
    }

    @DisplayName("스테이션 번호가 주어지면, 그 스테이션이 섹션 안에 있는지 확인한.")
    @ParameterizedTest
    @CsvSource({
            "1,true", "4,false", "3,true", "10,false",
    })
    void isStationIdExistTest(Long stationId, boolean expected) {
        //given : Csv Source

        //when
        boolean result = sections.isStationIdExist(stationId);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("정상적으로 구성 될 섹션의 스테이션 번호 2개가 주어지면, 그중 신설되어야 하는 스테이션 번호를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,4", "4,2,4", "7,3,7", "3,10,10", "1,10,10",
    })
    void getExtendedStationIdTest(Long upStationId, Long downStationId, Long expected) {
        //given : Csv Source

        //when
        Long result = sections.getExtendedStationId(upStationId, downStationId);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("새로운 구간이 주어졌을때, 해당 라인 상행점과 하행점이 모두 존재하는에 경우 오류를 반환한다.")
    @Test()
    void validateSectionSameStationTest() {
        //given
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when,then
        assertThrows(SectionSameSectionException.class,
                ()->sections.validateSection(upStationId,downStationId,distance));
    }

    @DisplayName("새로운 구간이 주어졌을때, 해당 라인 상행점과 하행점이 모두 존재하는에 않는 경우 오류를 반환한다.")
    @Test()
    void validateSectionNoStationTest() {
        //given
        Long upStationId = 4L;
        Long downStationId = 5L;
        int distance = 10;

        //when,then
        assertThrows(SectionNoStationException.class,
                ()->sections.validateSection(upStationId,downStationId,distance));
    }

    @DisplayName("새로운 구간이 주어지고, 하행점이 생성되는 상황에서 기존 상행점의 하행구간 길이가 새로운 길이보다 같거나 짧을 경우 오류를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,5", "1,5,6", "2,5,5", "2,6,6"
    })
    void validateSectionIllegalDistanceTest1(Long upStationId, Long downStationId, int distance) {
        //given @params

        //when,then
        assertThrows(SectionIllegalDistanceException.class,
                ()->sections.validateSection(upStationId,downStationId,distance));
    }

    @DisplayName("새로운 구간이 주어지고, 상행점이 생성되는 상황에서 기존 하행점의 상행구간 길이가 새로운 길이보다 같거나 짧을 경우 오류를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "4,3,5", "5,3,6", "4,2,5", "5,2,6"
    })
    void validateSectionIllegalDistanceTest2(Long upStationId, Long downStationId, int distance) {
        //given @params

        //when,then
        assertThrows(SectionIllegalDistanceException.class,
                ()->sections.validateSection(upStationId,downStationId,distance));
    }

    @DisplayName("구간 길이가 0이하일 경우 오류를 반환한")
    @ParameterizedTest
    @CsvSource({
            "4,3,0", "5,3,-1", "4,2,0", "5,2,-10"
    })
    void validateSectionIllegalDistanceTest3(Long upStationId, Long downStationId, int distance) {
        //given @params

        //when,then
        assertThrows(SectionIllegalDistanceException.class,
                ()->sections.validateSection(upStationId,downStationId,distance));
    }


    @DisplayName("새로운 구간이 주어지고, 생성이 가능한 경우일 경우 오류를 반환하지 않는다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,4", "1,5,3", "2,5,1", "2,6,2","4,3,3", "5,3,4", "4,2,4", "5,1,6",
    })
    void validateSectionTest(Long upStationId, Long downStationId, int distance) {
        //given @params

        //when,then
        assertDoesNotThrow(()->sections.validateSection(upStationId,downStationId,distance));
    }

    @DisplayName("하행점이 생성되는 경우, 상행점의 거리로 부터 주어진 거리만큼 더해진 상대적 위치를 하행점 위치로 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,4,4", "2,5,3,8", "3,5,10,20",
    })
    void calculateRelativeDistanceTest1(Long upStationId, Long downStationId, int distance, int expected) {
        //given @params

        //when
        int result = sections.calculateRelativeDistance(upStationId, downStationId, distance);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("상행점이 생성되는 경우, 하행점의 거리로 부터 주어진 거리만큼 빼진 상대적 위치를 상행점 위치로 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "4,1,4,-4", "5,2,3,2", "5,3,1,9",
    })
    void calculateRelativeDistanceTest2(Long upStationId, Long downStationId, int distance, int expected) {
        //given @params

        //when
        int result = sections.calculateRelativeDistance(upStationId, downStationId, distance);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("Sections가 주어졌을때, 각각의 섹션의 상대적 위치를 기준으로 정렬된 스테이션 아이디 리스트를 반환한다.")
    @Test
    void getSortedStationIdsByDistanceTest() {
        //given @BeforeEach로 주어진 sections

        //when
        List<Long> expected = new ArrayList<>();
        expected.add(1L);
        expected.add(2L);
        expected.add(3L);

        //then
        assertThat(sections.getSortedStationIdsByDistance()).containsExactlyElementsOf(expected);
    }

    @DisplayName("삭제할 스테이션 아이디가 주어졌을때, 섹션에 그 아이디가 존재하지 않을 경우 에러를 반환한다..")
    @Test
    void validateDeleteStationTest1() {
        //given
        Long stationId = 4L;

        //when,then
        assertThrows(SectionDeleteException.class,
                ()->sections.validateDeleteStation(stationId));
    }

    @DisplayName("삭제할 스테이션 아이디가 주어지고 현재 sections에 상행종점과 하행좀점뿐이 존재하지 않을 때 에러를 반환한다.")
    @Test
    void validateDeleteStationTest2() {
        //given
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(new Section(1L,1L,0));
        tempSections.add(new Section(1L,2L,5));
        Sections sectionsForValidate = new Sections(tempSections);
        Long stationId = 2L;

        //when,then
        assertThrows(SectionDeleteException.class,
                ()->sectionsForValidate.validateDeleteStation(stationId));
    }

    @DisplayName("삭제할 스테이션 아이디가 주어지고 삭제가 가능한 경우 에러를 반환하지 않는다.")
    @ParameterizedTest
    @CsvSource({
            "1","2","3",
    })
    void validateDeleteStationTest3(Long stationId) {
        //given

        //when,then
        assertDoesNotThrow(()->sections.validateDeleteStation(stationId));
    }

}
