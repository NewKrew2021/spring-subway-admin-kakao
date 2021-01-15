package subway.section;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import subway.exceptions.*;
import java.util.stream.Collectors;import org.springframework.util.ReflectionUtils;
import subway.exceptions.InvalidValueException;
import subway.station.*;

public class SectionDao {
    private static Long seq = 0L;
    private static List<Section> sections = new ArrayList<>();

    private static SectionDao instance;

    public static SectionDao getInstance() {
        if (instance == null)
            instance = new SectionDao();
        return instance;
    }

    private SectionDao() {
    }

    public Section save(Section newSection) {
        // 중복체크
        if(sections.stream().anyMatch(section ->
                section.getUpStationId() == newSection.getUpStationId() &&
                        section.getDownStationId() == newSection.getDownStationId()
        )){ throw new InvalidValueException(); }

        // 추가될때 상행하행 이어붙이기
        Section persistLine = createNewObject(newSection);
        for(int i=0; i <sections.size(); i++){
            Section savedSection = sections.get(i);
            if(savedSection.getUpStationId() == newSection.getUpStationId()) {
                if(savedSection.getDistance() <= newSection.getDistance()){
                    throw new InvalidValueException();
                }
                savedSection.setUpStationId(newSection.getDownStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
            if(savedSection.getDownStationId() == newSection.getUpStationId()){
                if(savedSection.getDistance() <= newSection.getDistance()){
                    throw new InvalidValueException();
                }
                savedSection.setDownStationId(newSection.getUpStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
        }

        // 새로운 종점이 생기는 경우 이어붙이기
        for(int i = 0; i < sections.size(); i++) {
            if(sections.get(i).getUpStationId() == newSection.getDownStationId()){
                    sections.add(i, persistLine);
                    return persistLine;
            }
        }

        // 동떨어진녀석이 여기까지옴
        if(sections.stream().anyMatch(section -> section.getLineId() == newSection.getLineId())){
            throw new InvalidValueException();
        }
        // 처음인 녀석
        sections.add(persistLine);
        return persistLine;
    }

    public List<Section> findAll() {
        return sections;
    }

    public Section findById(Long id) {
        return sections.stream()
                .filter(val -> val.getId() == id)
                .collect(Collectors.toList()).get(0);
    }

    public void deleteById(Long lineId, Long stationId) {
        List<Section> line = sections.stream()
                .filter(station -> station.getLineId() == lineId)
                .collect(Collectors.toList());

        // 구간이 1개인 노선인 경우
        if (line.size() == 1){
            throw new InvalidValueException();
        }

        // 가장 왼쪽인 경우
        if (line.get(0).getUpStationId() == stationId){
            deleteById(line.get(0).getId());
            return;
        }

        // 가장 오른쪽인 경우
        if (line.get(line.size()-1).getDownStationId() == stationId){
            deleteById(line.get(line.size()-1).getId());
            return;
        }

        // 중간에 있는 경우
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).getDownStationId() == stationId) {
                line.get(i).setDownStationId(line.get(i+1).getDownStationId());
                line.get(i).setDistance(line.get(i).getDistance() + line.get(i+1).getDistance());
                deleteById(line.get(i+1).getId());
                return;
            }
        }

    }

    public void deleteById(Long sectionId){
        sections.removeIf(it -> it.getId().equals(sectionId));
    }

    public boolean contain(Long stationId){
        return sections.stream()
                .anyMatch(section -> section.getUpStationId() == stationId || section.getDownStationId() == stationId);
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    public Long ifDownIdExist(Long downId) {
        if (sections.stream().filter(section -> section.getDownStationId() == downId).collect(Collectors.toList()).size() == 0)
            return -1L;
        return sections.stream().filter(section -> section.getDownStationId() == downId).collect(Collectors.toList()).get(0).getId();
    }

    public Long ifUpIdExist(Long upId) {
        if (sections.stream().filter(section -> section.getUpStationId() == upId).collect(Collectors.toList()).size() == 0)
            return -1L;
        return sections.stream().filter(section -> section.getUpStationId() == upId).collect(Collectors.toList()).get(0).getId();
    }
}
