package subway.line.entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Lines {
    private final List<Line> lines;

    public Lines(List<Line> lines) {
        validate(lines);
        this.lines = Collections.unmodifiableList(lines);
    }

    private void validate(List<Line> lines) {
        if (isEmpty(lines)) {
            throw new IllegalArgumentException("비어있는 지하철 노선 리스트를 입력받을 수 없습니다.");
        }
        if (hasDuplicateName(lines)) {
            throw new IllegalArgumentException("동일한 이름을 가진 지하철 노선을 입력받을 수 없습니다.");
        }
    }

    private boolean isEmpty(List<Line> lines) {
        return lines == null || lines.isEmpty();
    }

    private boolean hasDuplicateName(List<Line> lines) {
        return lines.stream()
                .map(Line::getName)
                .distinct()
                .count() != lines.size();
    }

    public Stream<Line> stream() {
        return lines.stream();
    }
}
