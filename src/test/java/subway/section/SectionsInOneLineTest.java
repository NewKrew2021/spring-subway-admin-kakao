package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exceptions.IllegalSectionSave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SectionsInOneLine 클래스")
public class SectionsInOneLineTest {
    Long 신분당선 = 1L;
    Long 강남역 = 4L;
    Long 판교역 = 3L;
    Long 정자역 = 2L;
    Long 미금역 = 1L;
    Long 동천역 = 5L;
    Section 강남_판교 = new Section(1L, 신분당선, 강남역, 판교역, 100);
    Section 판교_정자 = new Section(2L, 신분당선, 판교역, 정자역, 100);
    Section 정자_미금 = new Section(3L, 신분당선, 정자역, 미금역, 100);
    Section 미금_동천 = new Section(4L, 신분당선, 미금역, 동천역, 100);

    Section 강남_정자 = new Section(5L, 신분당선, 강남역, 정자역, 200);

    @DisplayName("객체 생성")
    @Test
    public void create() {
        List<Section> sectionList = Arrays.asList(강남_판교, 판교_정자, 정자_미금, 미금_동천);
        assertThatCode(() -> new SectionsInOneLine(sectionList)).doesNotThrowAnyException();
    }

    @DisplayName("라인에 section이 하나도 존재하지 않을때, 저장을 시도")
    @Test
    public void saveWhenNoSectionsExist() {
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(Arrays.asList());

        assertThatCode(() -> {
            sectionsInOneLine.validateSave(강남_판교);
        }).doesNotThrowAnyException();
    }

    @DisplayName("동일한 내용을 가지는 section을 다시 저장하는 경우")
    @Test
    public void saveSameSectionAgain() {
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_판교);
        sectionList.add(판교_정자);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);

        assertThatThrownBy(() -> {
            sectionsInOneLine.validateSave(판교_정자) ;
        }).isInstanceOf(IllegalSectionSave.class);
    }

    @DisplayName("line에 연결되지 않은 section을 save")
    @Test
    public void saveUnconnectedSection() {
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_판교);
        sectionList.add(판교_정자);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);

        assertThatThrownBy(() -> {
            sectionsInOneLine.validateSave(미금_동천);
        }).isInstanceOf(IllegalSectionSave.class);
    }

    @DisplayName("update되어야할 section을 가져온다.")
    @Test
    public void getSectionToBeUpdatedTest() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_정자);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);

        Section expectedUpdatedSection = 강남_정자.subtractBasedOnUpStation(강남_판교);

        //when
        Section updatedSection = sectionsInOneLine.getSectionToBeUpdated(강남_판교);

        //then
        assertThat(updatedSection).isEqualTo(expectedUpdatedSection);
    }

    @DisplayName("정렬된 station들을 가져온다.")
    @Test
    public void getSortedStationsTest() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_정자);
        sectionList.add(정자_미금);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);
        List<Long> expectedResult = Arrays.asList(강남역, 정자역, 미금역);

        //when
        List<Long> stationIds = sectionsInOneLine.getSortedStations();

        //then
        assertThat(stationIds).isEqualTo(expectedResult);
    }

    @DisplayName("특정 stationId를 가지는 section들을 가져온다.")
    @Test
    public void getSectionsThatContainTest() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_정자);
        sectionList.add(정자_미금);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);

        //when
        List<Section> testResult = sectionsInOneLine.getSectionsThatContain(정자역);

        //then
        assertThat(testResult).isEqualTo(sectionList);
    }

    @DisplayName("line에 존재하는 모든 stationsId들을 가져온다.")
    @Test
    public void getStationIdsTest() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(강남_정자);
        sectionList.add(정자_미금);
        SectionsInOneLine sectionsInOneLine = new SectionsInOneLine(sectionList);

        //when
        List<Long> stationIds = sectionsInOneLine.getStationList();

        //then
        assertThat(stationIds).contains(강남역);
        assertThat(stationIds).contains(정자역);
        assertThat(stationIds).contains(미금역);
    }
}