package subway.section;

class Section {

    static final long TERMINAL_ID = -1;
    static final int INF = Integer.MAX_VALUE;

    private static final int DISTANCE_THRESHOLD = 0;
    private static final String SAME_UP_DOWN_STATION_EXCEPTION_MESSAGE = "출발역과 도착역은 같을 수 없습니다.";
    private static final String DISTANCE_LESS_THAN_ONE_EXCEPTION_MESSAGE = "거리는 0보다 커야 합니다.";
    private static final String MUST_SAME_LINE_EXCEPTION_MESSAGE = "같은 노선이어야 합니다.";
    private static final String DISTANCE_SHOULD_LONGER_THAN_OTHER_EXCEPTION_MESSAGE = "구간의 거리는 현재 구간의 거리보다 더 짧아야 합니다.";
    private static final String UP_OR_DOWN_STATION_MUST_SAME_EXCEPTION_MESSAGE = "상/하행역 중 단 하나만 일치해야 합니다.";

    private final Long id;
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance) {
        validate(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validate(long upStationId, long downStationId, int distance) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException(SAME_UP_DOWN_STATION_EXCEPTION_MESSAGE);
        }

        if (distance <= DISTANCE_THRESHOLD) {
            throw new IllegalArgumentException(DISTANCE_LESS_THAN_ONE_EXCEPTION_MESSAGE);
        }
    }

    public Section subtractWith(Section other) {
        validateSubtract(other);

        int differenceOfDistance = calculateDistanceDifference(other);
        if (upStationId == other.upStationId) {
            return new Section(lineId, other.downStationId, downStationId, differenceOfDistance);
        }
        return new Section(lineId, upStationId, other.upStationId, differenceOfDistance);
    }

    private void validateSubtract(Section other) {
        if (lineId != other.lineId) {
            throw new IllegalArgumentException(MUST_SAME_LINE_EXCEPTION_MESSAGE);
        }
        if (distance <= other.distance) {
            throw new IllegalArgumentException(DISTANCE_SHOULD_LONGER_THAN_OTHER_EXCEPTION_MESSAGE);
        }
        if (upStationId == other.upStationId && downStationId == other.downStationId) {
            throw new IllegalArgumentException(UP_OR_DOWN_STATION_MUST_SAME_EXCEPTION_MESSAGE);
        }
        if (upStationId != other.upStationId && downStationId != other.downStationId) {
            throw new IllegalArgumentException(UP_OR_DOWN_STATION_MUST_SAME_EXCEPTION_MESSAGE);
        }
    }

    private int calculateDistanceDifference(Section other) {
        if (distance == INF || other.distance == INF) {
            return INF;
        }
        return distance - other.distance;
    }

    public Section mergeWith(Section other) {
        validateMerge(other);

        int totalDistance = calculateTotalDistance(other);
        if (downStationId == other.upStationId) {
            return new Section(lineId, upStationId, other.downStationId, totalDistance);
        }
        return new Section(lineId, other.upStationId, downStationId, totalDistance);
    }

    private void validateMerge(Section other) {
        if (lineId != other.lineId) {
            throw new IllegalArgumentException(MUST_SAME_LINE_EXCEPTION_MESSAGE);
        }
        if (downStationId != other.upStationId && upStationId != other.downStationId) {
            throw new IllegalArgumentException("연속된 구간이어야 합니다.");
        }
    }

    private int calculateTotalDistance(Section other) {
        if (distance == INF || other.distance == INF) {
            return INF;
        }
        return distance + other.distance;
    }

    public boolean hasSameUpStation(Section other) {
        return upStationId == other.upStationId;
    }

    public boolean hasSameDownStation(Section other) {
        return downStationId == other.downStationId;
    }

    public boolean isUpTerminal() {
        return upStationId == TERMINAL_ID;
    }

    public boolean isDownTerminal() {
        return downStationId == TERMINAL_ID;
    }

    public boolean containsStation(Long stationId) {
        return upStationId == stationId || downStationId == stationId;
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
