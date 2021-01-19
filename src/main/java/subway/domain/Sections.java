package subway.domain;

import java.util.List;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections){
        this.sections=sections;
    }

    private boolean extracted(Line nowLine, Section newSection, List<Section> sectionListFromNowLine) {
        if (isMatchedOnlyUpEndStation(nowLine, newSection)) {
            sectionDao.save(newSection);
            nowLine.setUpStationId(newSection.getUpStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }
        if (isMatchedOnlyDownEndStation(nowLine, newSection)) {
            sectionDao.save(newSection);
            nowLine.setDownStationId(newSection.getDownStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }
        for (Section oldSection : sectionListFromNowLine) {
            if (canInsertMatchingUpStation(oldSection, newSection)) {
                Section modifiedSection = new Section(oldSection.getId(), oldSection.getLineId(), newSection.getDownStationId(), oldSection.getDownStationId(), oldSection.getDistance() - newSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                sectionDao.save(newSection);
                return true;
            }
            if (canInsertMatchingDownStation(oldSection, newSection)) {
                Section modifiedSection = new Section(oldSection.getId(), oldSection.getLineId(), oldSection.getUpStationId(), newSection.getUpStationId(), oldSection.getDistance() - newSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                sectionDao.save(newSection);
                return true;
            }
        }
        return false;
    }




}
