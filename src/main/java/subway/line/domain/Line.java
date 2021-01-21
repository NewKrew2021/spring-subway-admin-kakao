package subway.line.domain;

import org.apache.commons.lang3.StringUtils;
import subway.line.vo.LineResultValue;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id) {
        this(id, "unused", "unused");
    }

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;

        checkAreValidArguments();
    }

    public LineResultValue toResultValue() {
        return new LineResultValue(id, name, color);
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    private void checkAreValidArguments() {
        if (isNegativeID()) {
            throw new IllegalArgumentException("Line ID cannot be negative");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Line name should not be null or blank");
        }

        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("Line color should not be null or blank");
        }
    }

    private boolean isNegativeID() {
        return id < 0;
    }
}
