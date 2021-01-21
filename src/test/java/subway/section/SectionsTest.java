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
import subway.section.domain.Section;
import subway.section.domain.Sections;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class SectionsTest {

    private Sections sections;

    @DisplayName("쿼리문을 order by로 하였기 때문에, 입력되는 section들의 relativeDistance의 order은 보장됨")
    @BeforeEach
    void create() {
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(new Section(1L,1L,0));
        tempSections.add(new Section(1L,2L,5));
        tempSections.add(new Section(1L,3L,10));
        sections = new Sections(tempSections);
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
                ()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
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
                ()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
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
                ()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
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
                ()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
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
                ()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
    }


    @DisplayName("새로운 구간이 주어지고, 생성이 가능한 경우일 경우 오류를 반환하지 않는다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,4", "1,5,3", "2,5,1", "2,6,2","4,3,3", "5,3,4", "4,2,4", "5,1,6",
    })
    void validateSectionTest(Long upStationId, Long downStationId, int distance) {
        //given @params

        //when,then
        assertDoesNotThrow(()->sections.validateAndGenerateStrategy(upStationId,downStationId,distance));
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
