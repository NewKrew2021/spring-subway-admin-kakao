package subway.line;

import java.util.List;

public class Lines {
    private final List<Line> lines;

    public Lines(List<Line> lines) {
        checkAlreadyExist(lines);
        this.lines = lines;
    }

    public void checkAlreadyExist(List<Line> lines) {
        if (lines.size() > 0) {
            throw new LineAlreadyExistException();
        }
    }
}
