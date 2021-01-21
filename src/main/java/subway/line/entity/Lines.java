package subway.line.entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Lines {
    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = Collections.unmodifiableList(lines);
    }

    public Stream<Line> stream() {
        return lines.stream();
    }
}
