package subway.section.domain;

public class DownSideSectionGenerator implements SectionGenerateStrategy {

    private static final int DOWN_SIDE_NEXT_INDEX = 1;

    private final long lineId;
    private final long startStationId;
    private final long stationId;
    private final int distance;
    private final int lastIndex;

    private DownSideSectionGenerator(long lineId, long startStationId, long stationId, int distance, int lastIndex) {
        this.lineId = lineId;
        this.startStationId = startStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.lastIndex = lastIndex;
    }

    public static DownSideSectionGenerator from(SectionCreateValue createValue, int lastIndex) {
        return new DownSideSectionGenerator(
                createValue.getLineId(),
                createValue.getUpStationId(),
                createValue.getDownStationId(),
                createValue.getDistance(),
                lastIndex
        );
    }

    @Override
    public long getStartStationId() {
        return startStationId;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public boolean isNotTerminalIndex(int idx) {
        return idx != lastIndex;
    }

    @Override
    public int getNextIndexOf(int idx) {
        return idx + DOWN_SIDE_NEXT_INDEX;
    }

    @Override
    public Section createFrom(Section section) {
        return new Section(lineId, stationId, section.calculateNextDownPosition(distance));
    }
}
