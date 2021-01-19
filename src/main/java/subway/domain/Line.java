package subway.domain;

import subway.exception.IllegalStationException;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(String name, String color, Long upStationId, Long downStationId) {
        if (checkProblemStationId(upStationId, downStationId)) {
            throw new IllegalStationException();
        }
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId) {
        this(name, color, upStationId, downStationId);
        this.id = id;
    }

    private boolean checkProblemStationId(Long upStationId, Long downStationId) {
        if (upStationId < 0) {
            return true;
        }
        if (downStationId < 0) {
            return true;
        }
        return upStationId.equals(downStationId);
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

}
