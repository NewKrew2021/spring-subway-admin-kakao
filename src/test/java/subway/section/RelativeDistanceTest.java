package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class RelativeDistanceTest {

    @DisplayName("상행역 설치 시, distance 를 음수로 환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,-1", "0,0", "10,-10",
    })
    void upStationDistanceTest(int distance, int expected) {
        //when,then
        assertThat(RelativeDistance.upStationDistance(distance)).isEqualTo(expected);
    }

    @DisplayName("하행역 설치 시, distance 를 그대로 환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,1", "0,0", "10,10",
    })
    void downStationDistanceTest(int distance, int expected) {
        //when,then
        assertThat(RelativeDistance.downStationDistance(distance)).isEqualTo(expected);
    }

    @DisplayName("역 설치 시, 현재 기준역의 상대적 거리에 주어진 distance를 더한 값을 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,1,2", "0,-10,-10", "10,10,20",
    })
    void calculateRelativeDistanceTest(int standardRelativeDistance ,int distance, int expected) {
        //when,then
        assertThat(new RelativeDistance(standardRelativeDistance)
                .calculateRelativeDistance(distance))
                .isEqualTo(expected);
    }

    @DisplayName("두 RelativeDistance 객체가 주어지면, 그 차이를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,-3", "1,5,-4", "10,9,1", "2,6,-4", "0,0,0"
    })
    void calculateDistanceDifferenceTest(int relativeDistanceStandard, int relativeDistanceCompare, int expected) {
        //when,then
        assertThat(new RelativeDistance(relativeDistanceStandard)
                .calculateDistanceDifference(new RelativeDistance(relativeDistanceCompare)))
                .isEqualTo(expected);
    }

    @DisplayName("두개의 상대적인 위치와 RelativeDistance객체가 주어지면, 객체가 그 구간 사이에 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1,4,4,true", "1,5,2,true", "-10,9,15,false", "2,6,-4,false", "0,1,-1,false"
    })
    void isBetweenTest(int upStationRelativeDistance, int downStationRelativeDistance,
            int relativeDistanceStandard, boolean expected) {
        //when,then
        assertThat(new RelativeDistance(relativeDistanceStandard)
                .isBetween(upStationRelativeDistance,downStationRelativeDistance))
                .isEqualTo(expected);
    }



}
