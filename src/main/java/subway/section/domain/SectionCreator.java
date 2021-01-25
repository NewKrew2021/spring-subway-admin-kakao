package subway.section.domain;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Component
public class SectionCreator {

    private static final int SINGLE_LENGTH = 1;
    private static final int FIRST_AND_ONLY_INDEX = 0;

    private final List<SectionCreateStrategy> sectionCreateStrategies;

    public SectionCreator() {
        sectionCreateStrategies = Arrays.asList(
                new DownSideCreator(),
                new UpSideCreator()
        );
    }

    public void addStrategy(SectionCreateStrategy strategy) {
        sectionCreateStrategies.add(strategy);
    }

    public Section getNextSection(Sections sections, SectionCreateValue createValue) {
        return sectionCreateStrategies.stream()
                .filter(sectionCreateStrategy -> sectionCreateStrategy.isSupport(sections, createValue))
                .collect(toSingleStrategy())
                .create(sections, createValue);
    }

    private Collector<SectionCreateStrategy, ?, SectionCreateStrategy> toSingleStrategy() {
        return collectingAndThen(toList(), strategies -> {
            if (strategies.size() < SINGLE_LENGTH) {
                throw new IllegalArgumentException("새로운 구간을 생성할 수 없습니다");
            }
            if (strategies.size() > SINGLE_LENGTH) {
                throw new IllegalArgumentException("새로운 구간을 생성할 수 있는 가짓수가 여러개입니다");
            }
            return strategies.get(FIRST_AND_ONLY_INDEX);
        });
    }
}
