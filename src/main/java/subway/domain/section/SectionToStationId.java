package subway.domain.section;

@FunctionalInterface
public interface SectionToStationId {
    Long getStationId(Section section);
}
