package subway.line;

@FunctionalInterface
public interface SectionToStationId {
    public Long getStationId(Section section);
}
