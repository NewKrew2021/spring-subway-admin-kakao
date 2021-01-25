package subway.section.domain;

public interface SectionCreateStrategy {

    boolean isSupport(Sections sections, SectionCreateValue createValue);

    Section create(Sections sections, SectionCreateValue sectionCreateValue);
}
