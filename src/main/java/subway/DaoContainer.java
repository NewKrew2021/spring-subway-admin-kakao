package subway;

import subway.line.LineDao;
import subway.line.SectionDao;
import subway.station.StationDao;

public class DaoContainer {
    private static StationDao stationDao;
    private static LineDao lineDao;
    private static SectionDao sectionDao;

    public static StationDao getStationDao() {
        if (stationDao == null) {
            stationDao = new StationDao();
        }
        return stationDao;
    }

    public static LineDao getLineDao() {
        if (lineDao == null) {
            lineDao = new LineDao();
        }
        return lineDao;
    }

    public static SectionDao getSectionDao() {
        if (sectionDao == null) {
            sectionDao = new SectionDao();
        }
        return sectionDao;
    }

    public static void refreshAll() {
        stationDao = null;
        lineDao = null;
        sectionDao = null;
    }

}
