package subway.line;

import java.util.*;

public class SectionDao {
    private Map<Long, List<Section>> sections = new HashMap<>();

    private static SectionDao sectionDao;

    private SectionDao() {}

    public static SectionDao getInstance() {
        if (sectionDao == null) {
            sectionDao = new SectionDao();
        }
        return sectionDao;
    }


    private void checkSameStations(Section newSection, Section oldSection) {
        if (newSection.getUpStationId() == oldSection.getUpStationId() && newSection.getDownStationId() == oldSection.getDownStationId()) {
            throw new IllegalArgumentException();
        }
    }

    private void checkDistanceValidation(int newDistance, int oldDistance){
        if(newDistance >= oldDistance){
            throw new IllegalArgumentException();
        }
    }

    private void updateSection(int index, Section newSection) {
        List<Section> updateSections = this.sections.get(newSection.getLineId());
        updateSections.remove(index);
        updateSections.add(index, newSection);
    }

    private void insertSection(int index, Section section) {
        List<Section> sections = this.sections.get(section.getLineId());
        sections.add(index, section);
    }

    public void addSection(int index, Long id, Section section) {
        List<Section> updateSections = sections.get(section.getLineId());
        if(id == section.getUpStationId()){
            if(index == updateSections.size()){
                Section newSection = new Section(section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
                insertSection(index, newSection);
                return;
            }
            checkSameStations(updateSections.get(index), section);
            int oldDistance = updateSections.get(index).getDistance();
            int newDistance = oldDistance - section.getDistance();
            checkDistanceValidation(newDistance, oldDistance);
            insertSection(index, section);
            Section newSection = new Section(section.getLineId(), section.getDownStationId(), updateSections.get(index).getDownStationId(), newDistance);
            updateSection(index+1, newSection);
        }
        if (id == section.getDownStationId()) {
            if (index == 0) {
                Section newSection = new Section(section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
                insertSection(index, newSection);
                return;
            }
            checkSameStations(updateSections.get(index), section);
            int oldDistance = updateSections.get(index).getDistance();
            int newDistance = oldDistance - section.getDistance();
            checkDistanceValidation(newDistance, oldDistance);
            insertSection(index, section);
            Section newSection = new Section(section.getLineId(), updateSections.get(index).getUpStationId(), section.getUpStationId(), newDistance);
            updateSection(index-1, newSection);
        }
    }
    // bc
    // a c d ac cd -> ab bc cd
}
