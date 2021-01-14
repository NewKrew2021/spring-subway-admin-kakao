package subway;

import subway.line.LineDao;
import subway.section.SectionDao;
import subway.station.StationDao;

public class Container {
    public static LineDao lineDao = new LineDao();
    public static SectionDao sectionDao = new SectionDao();
    public static StationDao stationDao = new StationDao();
}
