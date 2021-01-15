package subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SectionDao {
    private static SectionDao sectionDao;
    private Long seq = 0L;
    private List<Section> sections = new LinkedList<>();

    public static SectionDao getSectionDao(){
        if(sectionDao==null){
            sectionDao=new SectionDao();
        }
        return sectionDao;
    }

    public void init(){
        sections = new ArrayList<>();
        seq = 0L;
    }

    public Section save(Section section){
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }



    public Section createNewObject(Section section){
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }



}
