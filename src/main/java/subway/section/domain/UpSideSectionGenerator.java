package subway.section.domain;

public class UpSideSectionGenerator implements SectionGenerateStrategy {

    private static final int UP_SIDE_NEXT_INDEX = -1;
    private static final int UP_SIDE_TERMINAL_INDEX = 0;

    private final long lineId;
    private final long startStationId;
    private final long stationId;
    private final int distance;

    private UpSideSectionGenerator(long lineId, long startStationId, long stationId, int distance) {
        this.lineId = lineId;
        this.startStationId = startStationId;
        this.stationId = stationId;
        this.distance = distance;
    }

    public static UpSideSectionGenerator from(SectionCreateValue createValue) {
        return new UpSideSectionGenerator(
                createValue.getLineId(),
                createValue.getDownStationId(),
                createValue.getUpStationId(),
                createValue.getDistance()
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
        return idx != UP_SIDE_TERMINAL_INDEX;
    }

    @Override
    public int getNextIndexOf(int idx) {
        return idx + UP_SIDE_NEXT_INDEX;
    }

    @Override
    public Section createFrom(Section section) {
        return new Section(lineId, stationId, section.calculateNextUpPosition(distance));
    }
}
