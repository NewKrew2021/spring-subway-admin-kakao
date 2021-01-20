package subway.section.domain;

public interface SectionGenerateStrategy {

    long getStartStationId();

    int getDistance();

    boolean isNotTerminalIndex(int idx);

    int getNextIndexOf(int idx);

    Section createFrom(Section section);
}
