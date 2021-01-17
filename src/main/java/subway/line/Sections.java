package subway.line;

import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sections {

    List<Section> sections = new ArrayList<>();

    public Sections(LineRequest lineRequest) {
        this.sections.add( new Section(0, lineRequest.getUpStationId(), lineRequest.getDistance()) );
        this.sections.add( new Section(lineRequest.getDistance(), lineRequest.getDownStationId(), 0) );
    }

    public SectionType matchStation(SectionRequest sectionRequest) {
        int distance = sectionRequest.getDistance();
        List<SectionType> sectionTypes = confirmSectionTypes(sectionRequest.getUpStationId() , sectionRequest.getDownStationId() );

        if( sectionTypes.size() != 1 || !sectionTypes.get(0).confirmDistance(distance, sections) ) {
            return SectionType.EXCEPTION;
        }

        return sectionTypes.get(0);
    }

    private List<SectionType> confirmSectionTypes(long upStationId, long downStationId) {
        return IntStream.range(0, sections.size())
                .mapToObj(i -> sections.get(i).sectionConfirm(upStationId, downStationId, i) )
                .filter(sectionType -> sectionType != SectionType.EXCEPTION)
                .collect(Collectors.toList());
    }

    public void addSection(SectionType sectionType, SectionRequest sectionRequest) {
        int upDistance = 0, downDistance = 0;
        int index = sectionType.getIndex();
        long id = 0;

        if( sectionType == SectionType.UP_STATION ) {
            upDistance = sectionRequest.getDistance();
            downDistance = sections.get(index).getDownDistance() != 0 ?  sections.get(index).getDownDistance() - sectionRequest.getDistance() : 0;
            id = sectionRequest.getDownStationId();
        }
        if ( sectionType == SectionType.DOWN_STATION ) {
            upDistance = sections.get(index).getUpDistance() != 0 ? sections.get(index).getUpDistance() - sectionRequest.getDistance() : 0;
            downDistance = sectionRequest.getDistance();
            id = sectionRequest.getUpStationId();
        }

        fixSection(index - sectionType.ordinal(), upDistance, downDistance, id);
    }

    public void fixSection(int index, int upDistance, int downDistance, long id) {


        if( index >= 0 && index < sections.size() ) {
            sections.get(index).setDownDistance(upDistance);
        }
        if( index + 1 >= 0 && index + 1 < sections.size() ) {
            sections.get(index + 1).setUpDistance(downDistance);
        }

        Section section = new Section(upDistance, id, downDistance);
        sections.add(index + 1 , section);

    }

}
